package com.biblioteca.domain.model.rental;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record RentalPeriod(LocalDate startDate, LocalDate endDate) {

    private static final int MINIMUM_DAYS = 5;

    public RentalPeriod {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days < MINIMUM_DAYS) {
            throw new IllegalArgumentException("Rental period must be at least " + MINIMUM_DAYS + " days");
        }
    }
}
