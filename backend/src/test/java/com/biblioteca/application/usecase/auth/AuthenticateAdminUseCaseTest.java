package com.biblioteca.application.usecase.auth;

import com.biblioteca.domain.model.admin.Admin;
import com.biblioteca.domain.repository.AdminRepository;
import com.biblioteca.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateAdminUseCaseTest {

    @Mock AdminRepository adminRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;

    @InjectMocks AuthenticateAdminUseCase useCase;

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        Admin admin = new Admin("admin", "hashed");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken("admin")).thenReturn("jwt-token");

        AuthenticateAdminUseCase.Output output =
                useCase.execute(new AuthenticateAdminUseCase.Input("admin", "secret"));

        assertThat(output.token()).isEqualTo("jwt-token");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(adminRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new AuthenticateAdminUseCase.Input("unknown", "pass")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void shouldThrowWhenPasswordIsWrong() {
        Admin admin = new Admin("admin", "hashed");
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new AuthenticateAdminUseCase.Input("admin", "wrong")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }
}
