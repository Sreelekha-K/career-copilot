package com.careercopilot.backend.controller;

import com.careercopilot.backend.dto.ResumeRequestDto;
import com.careercopilot.backend.dto.ResumeResponseDto;
import com.careercopilot.backend.service.ResumeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:4200")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping
    public ResumeResponseDto saveResume(@RequestBody ResumeRequestDto request) {
        return resumeService.saveResume(request);
    }

    @PostMapping("/upload")
    public ResumeResponseDto uploadResume(@RequestParam("file") MultipartFile file) {
        return resumeService.uploadResume(file);
    }

    @GetMapping
    public List<ResumeResponseDto> getAllResumes() {
        return resumeService.getAllResumes();
    }

    @GetMapping("/{id:\\d+}")
    public ResumeResponseDto getResumeById(@PathVariable Long id) {
        return resumeService.getResumeById(id);
    }

    @DeleteMapping("/{id:\\d+}")
    public void deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
    }
}