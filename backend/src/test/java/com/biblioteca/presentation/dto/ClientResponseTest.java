package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.client.Client;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ClientResponseTest {

    @Test
    void from_mapsAllClientFields() {
        Client client = new Client("Ana Lima", "ana@email.com", "11999990000");
        ClientResponse response = ClientResponse.from(client);

        assertThat(response.id()).isNull();
        assertThat(response.name()).isEqualTo("Ana Lima");
        assertThat(response.email()).isEqualTo("ana@email.com");
        assertThat(response.phone()).isEqualTo("11999990000");
    }
}
