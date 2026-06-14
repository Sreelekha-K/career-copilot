package com.careercopilot.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Career Copilot Backend Running!";
    }

    @GetMapping("/api/status")
    public String status() {
        return "Backend Connected Successfully";
    }
}