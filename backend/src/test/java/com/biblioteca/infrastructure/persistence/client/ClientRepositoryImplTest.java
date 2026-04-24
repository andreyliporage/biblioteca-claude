package com.biblioteca.infrastructure.persistence.client;

import com.biblioteca.domain.model.client.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(ClientRepositoryImpl.class)
class ClientRepositoryImplTest {

    @Autowired ClientRepositoryImpl clientRepository;

    private Client saved;

    @BeforeEach
    void setUp() {
        saved = clientRepository.save(new Client("Ana Lima", "ana@email.com", "11999990000"));
    }

    @Test
    void save_persistsClient() {
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Ana Lima");
    }

    @Test
    void findById_returnsClientWhenExists() {
        Optional<Client> result = clientRepository.findById(saved.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        assertThat(clientRepository.findById(999L)).isEmpty();
    }

    @Test
    void findByEmail_returnsClientWhenExists() {
        Optional<Client> result = clientRepository.findByEmail("ana@email.com");
        assertThat(result).isPresent();
    }

    @Test
    void findByEmail_returnsEmptyWhenNotExists() {
        assertThat(clientRepository.findByEmail("ghost@email.com")).isEmpty();
    }

    @Test
    void search_withNullQuery_returnsAllClients() {
        clientRepository.save(new Client("Bruno Santos", "bruno@email.com", null));
        List<Client> result = clientRepository.search(null);
        assertThat(result).hasSize(2);
    }

    @Test
    void search_withBlankQuery_returnsAllClients() {
        clientRepository.save(new Client("Bruno Santos", "bruno@email.com", null));
        List<Client> result = clientRepository.search("  ");
        assertThat(result).hasSize(2);
    }

    @Test
    void search_filtersByName() {
        clientRepository.save(new Client("Bruno Santos", "bruno@email.com", null));
        List<Client> result = clientRepository.search("ana");
        assertThat(result).hasSize(1).extracting(Client::getName).containsExactly("Ana Lima");
    }

    @Test
    void search_filtersByEmail() {
        clientRepository.save(new Client("Bruno Santos", "bruno@email.com", null));
        List<Client> result = clientRepository.search("bruno@email");
        assertThat(result).hasSize(1).extracting(Client::getName).containsExactly("Bruno Santos");
    }
}
