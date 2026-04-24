package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RentalResponseTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 24);

    @Test
    void from_mapsActiveRentalCorrectly() {
        Book book = new Book("LIV-00001", "Clean Code", "Author", "9780132350884");
        Client client = new Client("Ana Lima", "ana@email.com", "11999990000");
        Rental rental = new Rental(book, client, TODAY, TODAY.plusDays(7));

        RentalResponse response = RentalResponse.from(rental);

        assertThat(response.bookId()).isNull();
        assertThat(response.bookName()).isEqualTo("Clean Code");
        assertThat(response.bookCode()).isEqualTo("LIV-00001");
        assertThat(response.clientName()).isEqualTo("Ana Lima");
        assertThat(response.startDate()).isEqualTo(TODAY);
        assertThat(response.endDate()).isEqualTo(TODAY.plusDays(7));
        assertThat(response.returnedAt()).isNull();
        assertThat(response.active()).isTrue();
    }

    @Test
    void from_mapsReturnedRentalCorrectly() {
        Book book = new Book("LIV-00001", "Clean Code", "Author", "9780132350884");
        Client client = new Client("Ana Lima", "ana@email.com", "11999990000");
        Rental rental = new Rental(book, client, TODAY, TODAY.plusDays(7));
        rental.returnBook();

        RentalResponse response = RentalResponse.from(rental);

        assertThat(response.returnedAt()).isNotNull();
        assertThat(response.active()).isFalse();
    }
}
