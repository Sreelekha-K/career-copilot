package com.careercopilot.backend.service;

import com.careercopilot.backend.dto.ResumeRequestDto;
import com.careercopilot.backend.dto.ResumeResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeResponseDto saveResume(ResumeRequestDto request);

    ResumeResponseDto uploadResume(MultipartFile file);

    List<ResumeResponseDto> getAllResumes();

    ResumeResponseDto getResumeById(Long id);

    void deleteResume(Long id);
}