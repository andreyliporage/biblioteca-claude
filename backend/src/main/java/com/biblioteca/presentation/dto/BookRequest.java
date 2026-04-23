package com.biblioteca.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(@NotBlank String name, @NotBlank String author, @NotBlank String isbn) {}
