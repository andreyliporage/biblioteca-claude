package com.biblioteca.domain.model.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ClientTest {

    @Test
    void constructor_setsAllFields() {
        Client client = new Client("Ana Lima", "ana@email.com", "11999990000");
        assertThat(client.getName()).isEqualTo("Ana Lima");
        assertThat(client.getEmail()).isEqualTo("ana@email.com");
        assertThat(client.getPhone()).isEqualTo("11999990000");
        assertThat(client.getId()).isNull();
    }

    @Test
    void constructor_allowsNullPhone() {
        Client client = new Client("Ana Lima", "ana@email.com", null);
        assertThat(client.getPhone()).isNull();
    }
}
