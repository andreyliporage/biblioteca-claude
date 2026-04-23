package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.client.Client;

public record ClientResponse(Long id, String name, String email, String phone) {

    public static ClientResponse from(Client client) {
        return new ClientResponse(client.getId(), client.getName(), client.getEmail(), client.getPhone());
    }
}
