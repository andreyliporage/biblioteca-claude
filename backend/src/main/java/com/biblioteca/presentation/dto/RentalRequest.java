package com.biblioteca.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentalRequest(
        @NotNull Long bookId,
        @NotNull Long clientId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
