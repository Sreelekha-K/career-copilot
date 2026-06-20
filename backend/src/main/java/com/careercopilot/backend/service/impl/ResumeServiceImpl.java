package com.careercopilot.backend.service.impl;

import com.careercopilot.backend.dto.ResumeRequestDto;
import com.careercopilot.backend.dto.ResumeResponseDto;
import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.exception.ResourceNotFoundException;
import com.careercopilot.backend.repository.ResumeRepository;
import com.careercopilot.backend.service.ResumeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Override
    public ResumeResponseDto saveResume(ResumeRequestDto request) {

        ResumeEntity resumeEntity = new ResumeEntity();

        resumeEntity.setFileName(request.getFileName());
        resumeEntity.setFileType(request.getFileType());
        resumeEntity.setUploadedAt(LocalDateTime.now());

        ResumeEntity savedResume = resumeRepository.save(resumeEntity);

        return mapToResponseDto(savedResume);
    }

    @Override
    public List<ResumeResponseDto> getAllResumes() {

        List<ResumeEntity> resumes = resumeRepository.findAll();

        return resumes.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResumeResponseDto getResumeById(Long id) {

        ResumeEntity resumeEntity = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found with id " + id)
                );

        return mapToResponseDto(resumeEntity);
    }
    @Override
    public void deleteResume(Long id) {

        ResumeEntity resumeEntity = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));

        resumeRepository.delete(resumeEntity);
    }

    private ResumeResponseDto mapToResponseDto(ResumeEntity resumeEntity) {

        ResumeResponseDto response = new ResumeResponseDto();

        response.setId(resumeEntity.getId());
        response.setFileName(resumeEntity.getFileName());
        response.setFileType(resumeEntity.getFileType());
        response.setUploadedAt(resumeEntity.getUploadedAt());

        return response;
    }
}