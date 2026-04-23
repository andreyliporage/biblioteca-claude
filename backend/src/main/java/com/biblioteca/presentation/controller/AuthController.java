package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.auth.AuthenticateAdminUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticateAdminUseCase authenticateAdminUseCase;

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String token) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateAdminUseCase.Output output = authenticateAdminUseCase.execute(
                new AuthenticateAdminUseCase.Input(request.username(), request.password())
        );
        return ResponseEntity.ok(new LoginResponse(output.token()));
    }
}
