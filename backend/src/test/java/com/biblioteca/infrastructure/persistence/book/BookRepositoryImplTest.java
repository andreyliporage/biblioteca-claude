package com.biblioteca.infrastructure.persistence.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(BookRepositoryImpl.class)
class BookRepositoryImplTest {

    @Autowired BookRepositoryImpl bookRepository;

    private Book saved;

    @BeforeEach
    void setUp() {
        saved = bookRepository.save(new Book("LIV-00001", "Clean Code", "Robert C. Martin", "9780132350884"));
    }

    @Test
    void save_persistsBook() {
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("LIV-00001");
    }

    @Test
    void findById_returnsBookWhenExists() {
        Optional<Book> result = bookRepository.findById(saved.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Clean Code");
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        assertThat(bookRepository.findById(999L)).isEmpty();
    }

    @Test
    void count_returnsCorrectCount() {
        assertThat(bookRepository.count()).isEqualTo(1L);
        bookRepository.save(new Book("LIV-00002", "DDD", "Evans", "9780321125217"));
        assertThat(bookRepository.count()).isEqualTo(2L);
    }

    @Test
    void search_withNoFilters_returnsAllBooks() {
        bookRepository.save(new Book("LIV-00002", "DDD", "Evans", "9780321125217"));
        List<Book> result = bookRepository.search(null, null, null, null, null);
        assertThat(result).hasSize(2);
    }

    @Test
    void search_filtersByName() {
        bookRepository.save(new Book("LIV-00002", "Design Patterns", "Gamma", "9780201633610"));
        List<Book> result = bookRepository.search("clean", null, null, null, null);
        assertThat(result).hasSize(1).extracting(Book::getName).containsExactly("Clean Code");
    }

    @Test
    void search_filtersByAuthor() {
        bookRepository.save(new Book("LIV-00002", "Other", "Evans", "9780321125217"));
        List<Book> result = bookRepository.search(null, "martin", null, null, null);
        assertThat(result).hasSize(1).extracting(Book::getAuthor).containsExactly("Robert C. Martin");
    }

    @Test
    void search_filtersByIsbn() {
        List<Book> result = bookRepository.search(null, null, "9780132350884", null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void search_filtersByCode() {
        List<Book> result = bookRepository.search(null, null, null, "liv-00001", null);
        assertThat(result).hasSize(1);
    }

    @Test
    void search_filtersByStatus() {
        saved.rent();
        bookRepository.save(saved);
        List<Book> rented = bookRepository.search(null, null, null, null, BookStatus.RENTED);
        assertThat(rented).hasSize(1);
        List<Book> available = bookRepository.search(null, null, null, null, BookStatus.AVAILABLE);
        assertThat(available).isEmpty();
    }

    @Test
    void search_withBlankFilters_returnsAllBooks() {
        List<Book> result = bookRepository.search("", "  ", "", "", null);
        assertThat(result).hasSize(1);
    }
}
