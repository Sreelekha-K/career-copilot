package com.careercopilot.backend.config;

import com.careercopilot.backend.entity.User;
import com.careercopilot.backend.entity.UserRole;
import com.careercopilot.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("!prod")
public class DevelopmentAdminSeeder implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@careercopilot.com";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevelopmentAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();
        admin.setName("Career Copilot Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode("demo"));
        admin.setRole(UserRole.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);
    }
}
