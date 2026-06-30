package com.careercopilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JobAnalysisRequestDto {

    @NotNull(message = "resumeId is required")
    private Long resumeId;

    @NotBlank(message = "jobDescription is required")
    private String jobDescription;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
