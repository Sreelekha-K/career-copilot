package com.careercopilot.backend.service;

import com.careercopilot.backend.dto.AuthRequestDto;
import com.careercopilot.backend.dto.AuthResponseDto;
import com.careercopilot.backend.dto.RegisterRequestDto;
import com.careercopilot.backend.entity.User;
import com.careercopilot.backend.exception.AuthException;
import com.careercopilot.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDto(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponseDto login(AuthRequestDto request) {

        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDto(token, user.getId(), user.getName(), user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(normalizeEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
