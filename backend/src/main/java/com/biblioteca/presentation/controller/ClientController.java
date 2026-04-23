package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.client.RegisterClientUseCase;
import com.biblioteca.domain.repository.ClientRepository;
import com.biblioteca.presentation.dto.ClientRequest;
import com.biblioteca.presentation.dto.ClientResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final RegisterClientUseCase registerClientUseCase;
    private final ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<ClientResponse> register(@Valid @RequestBody ClientRequest request) {
        var client = registerClientUseCase.execute(
                new RegisterClientUseCase.Input(request.name(), request.email(), request.phone()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(client));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> search(@RequestParam(required = false) String query) {
        var clients = clientRepository.search(query);
        return ResponseEntity.ok(clients.stream().map(ClientResponse::from).toList());
    }
}
