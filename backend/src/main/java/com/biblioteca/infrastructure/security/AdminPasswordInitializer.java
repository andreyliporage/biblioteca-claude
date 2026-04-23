package com.biblioteca.infrastructure.security;

import com.biblioteca.domain.model.admin.Admin;
import com.biblioteca.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordInitializer implements CommandLineRunner {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 16;

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.existsAny()) return;

        String rawPassword = generatePassword();
        adminRepository.save(new Admin("admin", passwordEncoder.encode(rawPassword)));

        log.info("=================================================");
        log.info("Admin account created.");
        log.info("Username: admin");
        log.info("Password: {}", rawPassword);
        log.info("=================================================");
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
