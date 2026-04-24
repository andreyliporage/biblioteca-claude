package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.auth.AuthenticateAdminUseCase;
import com.biblioteca.infrastructure.security.JwtTokenProvider;
import com.biblioteca.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AuthenticateAdminUseCase authenticateAdminUseCase;
    @MockBean JwtTokenProvider jwtTokenProvider;

    @Test
    void login_returnsTokenOnSuccess() throws Exception {
        when(authenticateAdminUseCase.execute(any()))
                .thenReturn(new AuthenticateAdminUseCase.Output("jwt-token-value"));

        mockMvc.perform(post("/api/auth/login")                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"));
    }

    @Test
    void login_returns400WhenFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/auth/login")                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":""}
                                """))
                .andExpect(status().isBadRequest());

        verify(authenticateAdminUseCase, never()).execute(any());
    }

    @Test
    void login_returns400WhenUseCaseThrowsIllegalArgument() throws Exception {
        when(authenticateAdminUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(post("/api/auth/login")                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"wrong","password":"wrong"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
