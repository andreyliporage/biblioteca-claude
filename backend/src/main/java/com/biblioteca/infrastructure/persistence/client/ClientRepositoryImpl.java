package com.biblioteca.infrastructure.persistence.client;

import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpa;

    @Override
    public Client save(Client client) {
        return jpa.save(client);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public List<Client> search(String query) {
        if (query == null || query.isBlank()) return jpa.findAll();
        return jpa.search(query);
    }
}
