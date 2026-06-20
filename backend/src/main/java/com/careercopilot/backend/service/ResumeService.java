package com.careercopilot.backend.service;

import com.careercopilot.backend.dto.ResumeRequestDto;
import com.careercopilot.backend.dto.ResumeResponseDto;

import java.util.List;

public interface ResumeService {

    ResumeResponseDto saveResume(ResumeRequestDto request);

    List<ResumeResponseDto> getAllResumes();

    ResumeResponseDto getResumeById(Long id);

    void deleteResume(Long id);
}