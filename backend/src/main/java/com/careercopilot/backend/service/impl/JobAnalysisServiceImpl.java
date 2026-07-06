package com.careercopilot.backend.service.impl;

import com.careercopilot.backend.dto.JobAnalysisRequestDto;
import com.careercopilot.backend.dto.JobAnalysisResponseDto;
import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.entity.User;
import com.careercopilot.backend.exception.ResourceNotFoundException;
import com.careercopilot.backend.repository.ResumeRepository;
import com.careercopilot.backend.service.AiAnalysisService;
import com.careercopilot.backend.service.JobAnalysisService;
import com.careercopilot.backend.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class JobAnalysisServiceImpl implements JobAnalysisService {

    private final ResumeRepository resumeRepository;
    private final AiAnalysisService aiAnalysisService;
    private final UserService userService;

    public JobAnalysisServiceImpl(
            ResumeRepository resumeRepository,
            AiAnalysisService aiAnalysisService,
            UserService userService
    ) {
        this.resumeRepository = resumeRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.userService = userService;
    }

    @Override
    public JobAnalysisResponseDto analyze(JobAnalysisRequestDto request) {

        ResumeEntity resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found with id " + request.getResumeId())
                );

        verifyResumeAccess(resume);

        return aiAnalysisService.analyzeResumeForJob(resume, request.getJobDescription());
    }

    private void verifyResumeAccess(ResumeEntity resume) {

        User currentUser = userService.getCurrentUser();

        if (userService.isAdmin(currentUser)) {
            return;
        }

        User owner = resume.getOwner();

        if (owner == null || !owner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to analyze this resume.");
        }
    }
}
