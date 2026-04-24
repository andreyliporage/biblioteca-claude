package com.biblioteca.infrastructure.persistence.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.infrastructure.persistence.book.BookRepositoryImpl;
import com.biblioteca.infrastructure.persistence.client.ClientRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({RentalRepositoryImpl.class, BookRepositoryImpl.class, ClientRepositoryImpl.class})
class RentalRepositoryImplTest {

    @Autowired RentalRepositoryImpl rentalRepository;
    @Autowired BookRepositoryImpl bookRepository;
    @Autowired ClientRepositoryImpl clientRepository;

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 24);

    private Book book;
    private Client client;

    @BeforeEach
    void setUp() {
        book = bookRepository.save(new Book("LIV-00001", "Clean Code", "Author", "9780132350884"));
        client = clientRepository.save(new Client("Ana Lima", "ana@email.com", "11999990000"));
    }

    @Test
    void save_persistsRental() {
        Rental rental = rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        assertThat(rental.getId()).isNotNull();
        assertThat(rental.isActive()).isTrue();
    }

    @Test
    void findById_returnsRentalWithFetchedAssociations() {
        Rental saved = rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        Optional<Rental> result = rentalRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getBook().getName()).isEqualTo("Clean Code");
        assertThat(result.get().getClient().getName()).isEqualTo("Ana Lima");
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        assertThat(rentalRepository.findById(999L)).isEmpty();
    }

    @Test
    void findActive_returnsOnlyActiveRentals() {
        Rental active = rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));

        Book book2 = bookRepository.save(new Book("LIV-00002", "DDD", "Evans", "9780321125217"));
        Rental returned = rentalRepository.save(new Rental(book2, client, TODAY, TODAY.plusDays(7)));
        returned.returnBook();
        rentalRepository.save(returned);

        List<Rental> activeRentals = rentalRepository.findActive();
        assertThat(activeRentals).hasSize(1);
        assertThat(activeRentals.get(0).getId()).isEqualTo(active.getId());
    }

    @Test
    void findHistory_withNullFilters_returnsAllRentals() {
        rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        List<Rental> result = rentalRepository.findHistory(null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findHistory_filteredByBookId_returnsMatchingRentals() {
        rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        List<Rental> result = rentalRepository.findHistory(book.getId(), null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findHistory_filteredByClientId_returnsMatchingRentals() {
        rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        Client other = clientRepository.save(new Client("Bruno", "b@b.com", null));
        List<Rental> result = rentalRepository.findHistory(null, other.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findHistory_filteredByBothIds_returnsMatchingRentals() {
        rentalRepository.save(new Rental(book, client, TODAY, TODAY.plusDays(7)));
        List<Rental> result = rentalRepository.findHistory(book.getId(), client.getId());
        assertThat(result).hasSize(1);
    }
}
