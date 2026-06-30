package com.careercopilot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(
            ResourceNotFoundException ex) {

        return new ResponseEntity<>(
                Map.of("message", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResumeProcessingException.class)
    public ResponseEntity<Map<String, String>> handleResumeProcessing(
            ResumeProcessingException ex) {

        return new ResponseEntity<>(
                Map.of("message", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AiAnalysisException.class)
    public ResponseEntity<Map<String, String>> handleAiAnalysis(
            AiAnalysisException ex) {

        return new ResponseEntity<>(
                Map.of("message", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthException ex) {

        return new ResponseEntity<>(
                Map.of("message", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        return new ResponseEntity<>(
                Map.of("message", message),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(
            Exception ex) {

        return new ResponseEntity<>(
                Map.of("message", "Something went wrong. Please try again."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
