package com.biblioteca.application.usecase.client;

import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterClientUseCaseTest {

    @Mock ClientRepository clientRepository;

    @InjectMocks RegisterClientUseCase useCase;

    @Test
    void shouldRegisterClientSuccessfully() {
        when(clientRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Client result = useCase.execute(new RegisterClientUseCase.Input("John Doe", "john@example.com", "99999-0000"));

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyRegistered() {
        var existing = new Client("Jane", "jane@example.com", null);
        when(clientRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                useCase.execute(new RegisterClientUseCase.Input("Jane", "jane@example.com", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email already registered");

        verify(clientRepository, never()).save(any());
    }
}
