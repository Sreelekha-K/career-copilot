package com.careercopilot.backend.service;

import com.careercopilot.backend.dto.JobAnalysisResponseDto;
import com.careercopilot.backend.dto.AnonymousAnalysisPreviewDto;
import com.careercopilot.backend.entity.ResumeEntity;

public interface AiAnalysisService {

    JobAnalysisResponseDto analyzeResumeForJob(ResumeEntity resume, String jobDescription);

    AnonymousAnalysisPreviewDto analyzeAnonymousPreview(String resumeText, String fileName, String jobDescription);
}
