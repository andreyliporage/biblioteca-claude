package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.rental.Rental;

import java.time.LocalDate;

public record RentalResponse(
        Long id,
        Long bookId,
        String bookName,
        String bookCode,
        Long clientId,
        String clientName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate returnedAt,
        boolean active
) {

    public static RentalResponse from(Rental rental) {
        return new RentalResponse(
                rental.getId(),
                rental.getBook().getId(),
                rental.getBook().getName(),
                rental.getBook().getCode(),
                rental.getClient().getId(),
                rental.getClient().getName(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getReturnedAt(),
                rental.isActive()
        );
    }
}
