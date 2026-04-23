package com.biblioteca.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientRequest(@NotBlank String name, @NotBlank String email, String phone) {}
