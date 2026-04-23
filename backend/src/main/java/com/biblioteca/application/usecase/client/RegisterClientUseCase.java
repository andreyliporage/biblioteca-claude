package com.biblioteca.application.usecase.client;

import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterClientUseCase {

    private final ClientRepository clientRepository;

    public record Input(String name, String email, String phone) {}

    @Transactional
    public Client execute(Input input) {
        clientRepository.findByEmail(input.email()).ifPresent(c -> {
            throw new IllegalStateException("Email already registered");
        });
        return clientRepository.save(new Client(input.name(), input.email(), input.phone()));
    }
}
