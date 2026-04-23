package com.biblioteca.application.usecase.auth;

import com.biblioteca.domain.model.admin.Admin;
import com.biblioteca.domain.repository.AdminRepository;
import com.biblioteca.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateAdminUseCase {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public record Input(String username, String password) {}
    public record Output(String token) {}

    public Output execute(Input input) {
        Admin admin = adminRepository.findByUsername(input.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(input.password(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return new Output(jwtTokenProvider.generateToken(admin.getUsername()));
    }
}
