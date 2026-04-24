package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.book.Book;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BookResponseTest {

    @Test
    void from_mapsAllBookFields() {
        Book book = new Book("LIV-00001", "Clean Code", "Robert C. Martin", "9780132350884");
        BookResponse response = BookResponse.from(book);

        assertThat(response.id()).isNull();
        assertThat(response.code()).isEqualTo("LIV-00001");
        assertThat(response.name()).isEqualTo("Clean Code");
        assertThat(response.author()).isEqualTo("Robert C. Martin");
        assertThat(response.isbn()).isEqualTo("9780132350884");
        assertThat(response.status()).isEqualTo("AVAILABLE");
    }
}
