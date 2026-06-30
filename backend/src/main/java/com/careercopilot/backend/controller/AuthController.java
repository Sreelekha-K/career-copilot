package com.careercopilot.backend.controller;

import com.careercopilot.backend.dto.AuthRequestDto;
import com.careercopilot.backend.dto.AuthResponseDto;
import com.careercopilot.backend.dto.RegisterRequestDto;
import com.careercopilot.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
        return userService.login(request);
    }
}
