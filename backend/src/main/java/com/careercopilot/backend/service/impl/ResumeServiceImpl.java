package com.careercopilot.backend.service.impl;

import com.careercopilot.backend.dto.ResumeRequestDto;
import com.careercopilot.backend.dto.ResumeResponseDto;
import com.careercopilot.backend.entity.ResumeEntity;
import com.careercopilot.backend.entity.User;
import com.careercopilot.backend.exception.ResourceNotFoundException;
import com.careercopilot.backend.exception.ResumeProcessingException;
import com.careercopilot.backend.repository.ResumeRepository;
import com.careercopilot.backend.service.ResumeService;
import com.careercopilot.backend.service.UserService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private static final String DEFAULT_UPLOAD_DIR = "C:/projects/career-copilot/uploads/resumes";

    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final Path uploadDir;

    public ResumeServiceImpl(ResumeRepository resumeRepository, UserService userService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.uploadDir = resolveUploadDir();
    }

    @Override
    public ResumeResponseDto saveResume(ResumeRequestDto request) {

        ResumeEntity resumeEntity = new ResumeEntity();

        resumeEntity.setFileName(request.getFileName());
        resumeEntity.setFileType(request.getFileType());
        resumeEntity.setUploadedAt(LocalDateTime.now());
        resumeEntity.setOwner(userService.getCurrentUser());

        ResumeEntity savedResume = resumeRepository.save(resumeEntity);

        return mapToResponseDto(savedResume);
    }

    @Override
    public ResumeResponseDto uploadResume(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResumeProcessingException("Resume file is required");
        }

        Path filePath = null;

        try {
            Files.createDirectories(uploadDir);

            String originalFileName = file.getOriginalFilename();
            String safeFileName = System.currentTimeMillis() + "_" + (originalFileName == null ? "resume" : originalFileName);
            filePath = uploadDir.resolve(safeFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String resumeText = extractResumeText(filePath, file.getContentType(), originalFileName);

            ResumeEntity resumeEntity = new ResumeEntity();
            resumeEntity.setOwner(userService.getCurrentUser());
            resumeEntity.setFileName(originalFileName);
            resumeEntity.setFileType(file.getContentType());
            resumeEntity.setFileSize(file.getSize());
            resumeEntity.setFilePath(filePath.toString());
            resumeEntity.setResumeText(resumeText);
            resumeEntity.setUploadedAt(LocalDateTime.now());

            ResumeEntity savedResume = resumeRepository.save(resumeEntity);

            return mapToResponseDto(savedResume);

        } catch (IOException ex) {
            deleteUploadedFileIfPresent(filePath);
            throw new ResumeProcessingException("Failed to upload or parse resume file", ex);
        }
    }

    @Override
    public List<ResumeResponseDto> getAllResumes() {

        User currentUser = userService.getCurrentUser();

        List<ResumeEntity> resumes = userService.isAdmin(currentUser)
                ? resumeRepository.findAll()
                : resumeRepository.findByOwnerOrderByUploadedAtDesc(currentUser);

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

        verifyResumeAccess(resumeEntity);

        return mapToResponseDto(resumeEntity);
    }

    @Override
    public void deleteResume(Long id) {

        ResumeEntity resumeEntity = resumeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resume not found with id " + id)
                );

        verifyResumeAccess(resumeEntity);

        try {
            if (resumeEntity.getFilePath() != null) {
                Files.deleteIfExists(Path.of(resumeEntity.getFilePath()));
            }

            resumeRepository.delete(resumeEntity);

        } catch (IOException ex) {
            throw new ResumeProcessingException("Failed to delete resume file", ex);
        }
    }

    private ResumeResponseDto mapToResponseDto(ResumeEntity resumeEntity) {

        ResumeResponseDto response = new ResumeResponseDto();
        String preview = createResumeTextPreview(resumeEntity.getResumeText());

        response.setId(resumeEntity.getId());
        response.setFileName(resumeEntity.getFileName());
        response.setFileType(resumeEntity.getFileType());
        response.setUploadedAt(resumeEntity.getUploadedAt());
        response.setFileSize(resumeEntity.getFileSize());
        response.setFilePath(resumeEntity.getFilePath());
        response.setResumeTextPreview(preview);
        response.setParsedTextPreview(preview);

        return response;
    }

    private void verifyResumeAccess(ResumeEntity resumeEntity) {

        User currentUser = userService.getCurrentUser();

        if (userService.isAdmin(currentUser)) {
            return;
        }

        User owner = resumeEntity.getOwner();

        if (owner == null || !owner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this resume.");
        }
    }

    private String extractResumeText(Path filePath, String contentType, String originalFileName) throws IOException {

        if (isPdfFile(contentType, originalFileName)) {
            return extractPdfText(filePath);
        }

        if (isDocxFile(contentType, originalFileName)) {
            return extractDocxText(filePath);
        }

        if (isTxtFile(contentType, originalFileName)) {
            return Files.readString(filePath, StandardCharsets.UTF_8).trim();
        }

        return null;
    }

    private String extractPdfText(Path filePath) throws IOException {

        File pdfFile = filePath.toFile();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(document).trim();
        }
    }

    private String extractDocxText(Path filePath) throws IOException {

        try (
                InputStream inputStream = Files.newInputStream(filePath);
                XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)
        ) {
            return extractor.getText().trim();
        }
    }

    private boolean isPdfFile(String contentType, String originalFileName) {

        return "application/pdf".equalsIgnoreCase(contentType)
                || hasExtension(originalFileName, ".pdf");
    }

    private boolean isDocxFile(String contentType, String originalFileName) {

        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType)
                || hasExtension(originalFileName, ".docx");
    }

    private boolean isTxtFile(String contentType, String originalFileName) {

        return "text/plain".equalsIgnoreCase(contentType)
                || hasExtension(originalFileName, ".txt");
    }

    private boolean hasExtension(String fileName, String extension) {

        return fileName != null
                && fileName.toLowerCase(Locale.ROOT).endsWith(extension);
    }

    private String createResumeTextPreview(String resumeText) {

        if (resumeText == null || resumeText.isBlank()) {
            return null;
        }

        int previewLength = Math.min(resumeText.length(), 500);
        return resumeText.substring(0, previewLength);
    }

    private void deleteUploadedFileIfPresent(Path filePath) {

        if (filePath == null) {
            return;
        }

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Upload failure response is more important than cleanup failure here.
        }
    }

    private Path resolveUploadDir() {

        String configuredUploadDir = System.getenv("RESUME_UPLOAD_DIR");

        if (configuredUploadDir == null || configuredUploadDir.isBlank()) {
            configuredUploadDir = DEFAULT_UPLOAD_DIR;
        }

        return Path.of(configuredUploadDir);
    }
}
