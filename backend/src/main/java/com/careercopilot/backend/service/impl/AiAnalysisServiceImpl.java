package com.careercopilot.backend.service.impl;

import com.careercopilot.backend.dto.JobAnalysisResponseDto;
import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.exception.AiAnalysisException;
import com.careercopilot.backend.service.AiAnalysisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private static final int MAX_RESUME_CHARS = 14000;
    private static final int[] GEMINI_RETRY_DELAYS_MS = {2000, 5000, 10000};
    private static final String GEMINI_BUSY_MESSAGE = "Gemini is temporarily busy. Please try again in a minute.";
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String apiKey;

    public AiAnalysisServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.apiKey = System.getenv("GEMINI_API_KEY");
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public JobAnalysisResponseDto analyzeResumeForJob(ResumeEntity resume, String jobDescription) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new AiAnalysisException("Gemini API key is not configured. Set GEMINI_API_KEY in the environment.");
        }

        try {
            Map<String, Object> requestBody = buildGeminiRequest(resume, jobDescription);

            JsonNode response = sendGeminiRequestWithRetry(requestBody);

            String responseText = extractGeminiText(response);
            String json = stripJsonFences(responseText);
            JobAnalysisResponseDto analysis = objectMapper.readValue(json, JobAnalysisResponseDto.class);

            normalizeScores(analysis);
            return analysis;

        } catch (JsonProcessingException ex) {
            System.err.println("Gemini response could not be parsed into JobAnalysisResponseDto.");
            System.err.println("Gemini parse error class: " + ex.getClass().getName());
            System.err.println("Gemini parse error message: " + ex.getMessage());
            throw new AiAnalysisException("Gemini returned an invalid analysis format", ex);
        } catch (AiAnalysisException ex) {
            throw ex;
        } catch (Exception ex) {
            System.err.println("Gemini analysis request failed.");
            System.err.println("Gemini error class: " + ex.getClass().getName());
            System.err.println("Gemini error message: " + ex.getMessage());
            throw new AiAnalysisException("Failed to generate Gemini resume-job analysis", ex);
        }
    }

    private Map<String, Object> buildGeminiRequest(ResumeEntity resume, String jobDescription) {

        return Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", buildPrompt(resume, jobDescription))
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "responseMimeType", "application/json"
                )
        );
    }

    private JsonNode sendGeminiRequestWithRetry(Map<String, Object> requestBody) {

        for (int attempt = 0; attempt <= GEMINI_RETRY_DELAYS_MS.length; attempt++) {
            try {
                return restClient.post()
                        .uri(GEMINI_API_URL)
                        .header("x-goog-api-key", apiKey)
                        .body(requestBody)
                        .retrieve()
                        .body(JsonNode.class);
            } catch (RestClientResponseException ex) {
                if (!isGeminiUnavailable(ex)) {
                    throw ex;
                }

                logGeminiUnavailable(attempt, ex);

                if (attempt == GEMINI_RETRY_DELAYS_MS.length) {
                    throw new AiAnalysisException(GEMINI_BUSY_MESSAGE, ex);
                }

                sleepBeforeRetry(GEMINI_RETRY_DELAYS_MS[attempt]);
            }
        }

        throw new AiAnalysisException(GEMINI_BUSY_MESSAGE);
    }

    private boolean isGeminiUnavailable(RestClientResponseException ex) {

        String responseBody = ex.getResponseBodyAsString();
        String responseBodyLower = responseBody == null ? "" : responseBody.toLowerCase();

        return ex.getStatusCode().value() == 503
                || responseBodyLower.contains("unavailable")
                || responseBodyLower.contains("currently experiencing high demand")
                || responseBodyLower.contains("please try again later");
    }

    private void logGeminiUnavailable(int attempt, RestClientResponseException ex) {

        System.err.println("Gemini unavailable response received.");
        System.err.println("Gemini model: gemini-2.5-flash");
        System.err.println("Gemini attempt: " + (attempt + 1));
        System.err.println("Gemini status: " + ex.getStatusCode());
        System.err.println("Gemini error class: " + ex.getClass().getName());
        System.err.println("Gemini error message: " + ex.getMessage());
        System.err.println("Gemini response body: " + ex.getResponseBodyAsString());
    }

    private void sleepBeforeRetry(int delayMs) {

        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AiAnalysisException("Gemini analysis was interrupted. Please try again.", ex);
        }
    }

    private String buildPrompt(ResumeEntity resume, String jobDescription) {

        String resumeText = safe(resume.getResumeText());

        if (resumeText.isBlank()) {
            resumeText = "No extracted resume text is available. Use only file metadata and clearly state that deeper analysis requires extractable resume text.";
        }

        resumeText = truncate(resumeText, MAX_RESUME_CHARS);

        return """
                You are Career Copilot, an AI resume and job-description analyst for software engineers.

                Return JSON only. Do not include markdown, prose, explanations, or code fences.

                Required JSON structure:
                {
                  "atsScore": 0,
                  "jobMatchScore": 0,
                  "matchedSkills": [],
                  "missingSkills": [],
                  "improvementSuggestions": [],
                  "recommendedKeywords": [],
                  "refinedResume": "",
                  "learningRoadmap": [
                    {
                      "skill": "",
                      "whyImportant": "",
                      "learningSteps": []
                    }
                  ],
                  "technicalQuestions": [],
                  "behavioralQuestions": []
                }

                Rules:
                - atsScore must be an integer from 0 to 100.
                - jobMatchScore must be an integer from 0 to 100.
                - Refined resume must use only the user's real resume content.
                - Do not invent fake experience, fake projects, fake companies, fake metrics, fake education, or fake skills.
                - If a skill is missing, include it in missingSkills and learningRoadmap.
                - Do not add missing skills as experience in refinedResume.
                - Tailor everything to the selected resume and pasted job description.
                - Interview questions should be about 80 percent technical and 20 percent behavioral.
                - Keep output frontend-friendly, specific, and concise.

                Resume metadata:
                - Resume ID: %s
                - File name: %s
                - File type: %s
                - File size: %s bytes

                User resume content:
                %s

                Job description:
                %s
                """.formatted(
                resume.getId(),
                safe(resume.getFileName()),
                safe(resume.getFileType()),
                resume.getFileSize() == null ? "unknown" : resume.getFileSize(),
                resumeText,
                safe(jobDescription)
        );
    }

    private String extractGeminiText(JsonNode response) {

        if (response == null) {
            throw new AiAnalysisException("Gemini response was empty");
        }

        JsonNode textNode = response.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new AiAnalysisException("Gemini response did not contain text output");
        }

        return textNode.asText();
    }

    private String stripJsonFences(String responseText) {

        String json = responseText.trim();

        if (json.startsWith("```json")) {
            json = json.substring("```json".length()).trim();
        } else if (json.startsWith("```")) {
            json = json.substring("```".length()).trim();
        }

        if (json.endsWith("```")) {
            json = json.substring(0, json.length() - "```".length()).trim();
        }

        return json;
    }

    private void normalizeScores(JobAnalysisResponseDto analysis) {

        analysis.setAtsScore(clampScore(analysis.getAtsScore()));
        analysis.setJobMatchScore(clampScore(analysis.getJobMatchScore()));
    }

    private int clampScore(int score) {

        return Math.max(0, Math.min(100, score));
    }

    private String truncate(String value, int maxLength) {

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }

    private String safe(String value) {

        return value == null ? "" : value;
    }
}
