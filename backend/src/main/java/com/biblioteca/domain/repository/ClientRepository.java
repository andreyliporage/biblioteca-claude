package com.biblioteca.domain.repository;

import com.biblioteca.domain.model.client.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
    Optional<Client> findByEmail(String email);
    List<Client> search(String query);
}
