package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.client.RegisterClientUseCase;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.repository.ClientRepository;
import com.biblioteca.infrastructure.security.JwtTokenProvider;
import com.biblioteca.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(SecurityConfig.class)
class ClientControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean RegisterClientUseCase registerClientUseCase;
    @MockBean ClientRepository clientRepository;
    @MockBean JwtTokenProvider jwtTokenProvider;

    private static final Client CLIENT = new Client("Ana Lima", "ana@email.com", "11999990000");

    @Test
    @WithMockUser
    void register_returns201WithClient() throws Exception {
        when(registerClientUseCase.execute(any())).thenReturn(CLIENT);

        mockMvc.perform(post("/api/clients").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ana Lima","email":"ana@email.com","phone":"11999990000"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ana Lima"))
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    @WithMockUser
    void register_returns400WhenRequiredFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/clients").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"","phone":""}
                                """))
                .andExpect(status().isBadRequest());

        verify(registerClientUseCase, never()).execute(any());
    }

    @Test
    @WithMockUser
    void search_returnsAllClientsWhenNoQuery() throws Exception {
        when(clientRepository.search(null)).thenReturn(List.of(CLIENT));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana Lima"));
    }

    @Test
    @WithMockUser
    void search_filtersClientsByQuery() throws Exception {
        when(clientRepository.search("ana")).thenReturn(List.of(CLIENT));

        mockMvc.perform(get("/api/clients").param("query", "ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }
}
