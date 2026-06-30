package com.careercopilot.backend.service.impl;

import com.careercopilot.backend.dto.JobAnalysisRequestDto;
import com.careercopilot.backend.dto.JobAnalysisResponseDto;
import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.exception.ResourceNotFoundException;
import com.careercopilot.backend.repository.ResumeRepository;
import com.careercopilot.backend.service.AiAnalysisService;
import com.careercopilot.backend.service.JobAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class JobAnalysisServiceImpl implements JobAnalysisService {

    private final ResumeRepository resumeRepository;
    private final AiAnalysisService aiAnalysisService;

    public JobAnalysisServiceImpl(
            ResumeRepository resumeRepository,
            AiAnalysisService aiAnalysisService
    ) {
        this.resumeRepository = resumeRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    @Override
    public JobAnalysisResponseDto analyze(JobAnalysisRequestDto request) {

        ResumeEntity resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found with id " + request.getResumeId())
                );

        return aiAnalysisService.analyzeResumeForJob(resume, request.getJobDescription());
    }
}
