package com.careercopilot.backend.controller;

import com.careercopilot.backend.dto.JobAnalysisRequestDto;
import com.careercopilot.backend.dto.JobAnalysisResponseDto;
import com.careercopilot.backend.service.JobAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job-analysis")
@CrossOrigin(origins = "http://localhost:4200")
public class JobAnalysisController {

    private final JobAnalysisService jobAnalysisService;

    public JobAnalysisController(JobAnalysisService jobAnalysisService) {
        this.jobAnalysisService = jobAnalysisService;
    }

    @PostMapping("/analyze")
    public JobAnalysisResponseDto analyze(@Valid @RequestBody JobAnalysisRequestDto request) {
        return jobAnalysisService.analyze(request);
    }
}
