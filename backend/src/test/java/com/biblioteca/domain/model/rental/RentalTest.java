package com.biblioteca.domain.model.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.model.client.Client;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class RentalTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 24);

    private Book buildBook() {
        return new Book("LIV-00001", "Clean Code", "Author", "9780132350884");
    }

    private Client buildClient() {
        return new Client("John Doe", "john@email.com", "11999990000");
    }

    @Test
    void constructor_rentsBookAndSetsFields() {
        Book book = buildBook();
        Client client = buildClient();
        Rental rental = new Rental(book, client, TODAY, TODAY.plusDays(7));

        assertThat(rental.getBook()).isEqualTo(book);
        assertThat(rental.getClient()).isEqualTo(client);
        assertThat(rental.getStartDate()).isEqualTo(TODAY);
        assertThat(rental.getEndDate()).isEqualTo(TODAY.plusDays(7));
        assertThat(rental.getReturnedAt()).isNull();
        assertThat(book.getStatus()).isEqualTo(BookStatus.RENTED);
    }

    @Test
    void isActive_returnsTrueWhenNotReturned() {
        Rental rental = new Rental(buildBook(), buildClient(), TODAY, TODAY.plusDays(7));
        assertThat(rental.isActive()).isTrue();
    }

    @Test
    void returnBook_setsReturnedAtAndMakesBookAvailable() {
        Book book = buildBook();
        Rental rental = new Rental(book, buildClient(), TODAY, TODAY.plusDays(7));
        rental.returnBook();

        assertThat(rental.getReturnedAt()).isNotNull();
        assertThat(rental.isActive()).isFalse();
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void constructor_throwsWhenPeriodIsBelowMinimum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Rental(buildBook(), buildClient(), TODAY, TODAY.plusDays(2)));
    }
}
