package com.biblioteca.domain.model.rental;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RentalPeriodTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 24);

    @Test
    void shouldCreateValidPeriod() {
        RentalPeriod period = new RentalPeriod(TODAY, TODAY.plusDays(7));
        assertThat(period.startDate()).isEqualTo(TODAY);
        assertThat(period.endDate()).isEqualTo(TODAY.plusDays(7));
    }

    @Test
    void shouldAcceptExactlyFiveDays() {
        assertThatNoException().isThrownBy(
                () -> new RentalPeriod(TODAY, TODAY.plusDays(5)));
    }

    @Test
    void shouldThrowWhenPeriodBelowMinimum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RentalPeriod(TODAY, TODAY.plusDays(4)))
                .withMessageContaining("at least 5 days");
    }

    @Test
    void shouldThrowWhenEndDateEqualsStartDate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RentalPeriod(TODAY, TODAY))
                .withMessageContaining("End date must be after start date");
    }

    @Test
    void shouldThrowWhenEndDateBeforeStartDate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RentalPeriod(TODAY, TODAY.minusDays(1)))
                .withMessageContaining("End date must be after start date");
    }

    @Test
    void shouldThrowWhenStartDateIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RentalPeriod(null, TODAY.plusDays(7)))
                .withMessageContaining("required");
    }

    @Test
    void shouldThrowWhenEndDateIsNull() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new RentalPeriod(TODAY, null))
                .withMessageContaining("required");
    }
}
