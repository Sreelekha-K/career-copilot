package com.careercopilot.backend.service;

import com.careercopilot.backend.dto.JobAnalysisRequestDto;
import com.careercopilot.backend.dto.JobAnalysisResponseDto;

public interface JobAnalysisService {

    JobAnalysisResponseDto analyze(JobAnalysisRequestDto request);
}
