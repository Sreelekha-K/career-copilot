package com.careercopilot.backend.controller;

import com.careercopilot.backend.dto.AnonymousAnalysisPreviewDto;
import com.careercopilot.backend.exception.ResumeProcessingException;
import com.careercopilot.backend.service.AiAnalysisService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

@RestController
@RequestMapping({"/api/public/job-analysis", "/api/job-analysis"})
public class AnonymousJobAnalysisController {

    private final AiAnalysisService aiAnalysisService;

    public AnonymousJobAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/anonymous-preview")
    public AnonymousAnalysisPreviewDto analyzeAnonymousPreview(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription
    ) {

        validateRequest(file, jobDescription);

        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("career-copilot-anonymous-", getFileExtension(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            String resumeText = extractResumeText(tempFile, file.getOriginalFilename());
            return aiAnalysisService.analyzeAnonymousPreview(resumeText, file.getOriginalFilename(), jobDescription);

        } catch (IOException ex) {
            throw new ResumeProcessingException("Failed to process anonymous resume preview. Please try another PDF or DOCX file.", ex);
        } finally {
            deleteTempFile(tempFile);
        }
    }

    private void validateRequest(MultipartFile file, String jobDescription) {

        if (file == null || file.isEmpty()) {
            throw new ResumeProcessingException("Please upload a resume before analyzing.");
        }

        if (!isSupportedFile(file.getOriginalFilename())) {
            throw new ResumeProcessingException("Unsupported file type. Please upload a PDF or DOCX resume.");
        }

        if (jobDescription == null || jobDescription.isBlank()) {
            throw new ResumeProcessingException("Please paste a job description before analyzing.");
        }
    }

    private String extractResumeText(Path filePath, String fileName) throws IOException {

        if (hasExtension(fileName, ".pdf")) {
            return extractPdfText(filePath);
        }

        if (hasExtension(fileName, ".docx")) {
            return extractDocxText(filePath);
        }

        throw new ResumeProcessingException("Unsupported file type. Please upload a PDF or DOCX resume.");
    }

    private String extractPdfText(Path filePath) throws IOException {

        File pdfFile = filePath.toFile();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            return new PDFTextStripper().getText(document).trim();
        }
    }

    private String extractDocxText(Path filePath) throws IOException {

        try (
                InputStream inputStream = Files.newInputStream(filePath);
                XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)
        ) {
            return extractor.getText().trim();
        }
    }

    private boolean isSupportedFile(String fileName) {

        return hasExtension(fileName, ".pdf") || hasExtension(fileName, ".docx");
    }

    private boolean hasExtension(String fileName, String extension) {

        return fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(extension);
    }

    private String getFileExtension(String fileName) {

        if (hasExtension(fileName, ".pdf")) {
            return ".pdf";
        }

        if (hasExtension(fileName, ".docx")) {
            return ".docx";
        }

        return ".tmp";
    }

    private void deleteTempFile(Path tempFile) {

        if (tempFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException ex) {
            System.err.println("Failed to delete anonymous resume temp file: " + ex.getMessage());
        }
    }
}
