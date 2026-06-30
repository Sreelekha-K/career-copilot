package com.careercopilot.backend.dto;

import java.time.LocalDateTime;

public class ResumeResponseDto {

    private Long id;
    private String fileName;
    private String fileType;
    private LocalDateTime uploadedAt;
    private Long fileSize;
    private String filePath;
    private String resumeTextPreview;
    private String parsedTextPreview;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResumeTextPreview() {
        return resumeTextPreview;
    }

    public void setResumeTextPreview(String resumeTextPreview) {
        this.resumeTextPreview = resumeTextPreview;
    }

    public String getParsedTextPreview() {
        return parsedTextPreview;
    }

    public void setParsedTextPreview(String parsedTextPreview) {
        this.parsedTextPreview = parsedTextPreview;
    }
}
