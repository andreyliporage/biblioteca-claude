package com.biblioteca.domain.model.book;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ISBNTest {

    @Test
    void shouldAcceptValidIsbn10() {
        ISBN isbn = ISBN.of("0306406152");
        assertThat(isbn.value()).isEqualTo("0306406152");
    }

    @Test
    void shouldAcceptIsbn10WithCheckDigitX() {
        // 123456789X is a valid ISBN-10: sum=220, 220%11=0
        ISBN isbn = ISBN.of("123456789X");
        assertThat(isbn.value()).isEqualTo("123456789X");
    }

    @Test
    void shouldAcceptIsbn10WithLowercaseX() {
        ISBN isbn = ISBN.of("123456789x");
        assertThat(isbn.value()).isEqualTo("123456789x");
    }

    @Test
    void shouldAcceptValidIsbn13() {
        ISBN isbn = ISBN.of("9780132350884");
        assertThat(isbn.value()).isEqualTo("9780132350884");
    }

    @Test
    void shouldStripDashesFromIsbn13() {
        ISBN isbn = ISBN.of("978-0-13-235088-4");
        assertThat(isbn.value()).isEqualTo("9780132350884");
    }

    @Test
    void shouldStripSpacesFromIsbn10() {
        ISBN isbn = ISBN.of("0 306 406 152");
        assertThat(isbn.value()).isEqualTo("0306406152");
    }

    @Test
    void shouldThrowForInvalidIsbn10Checksum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ISBN.of("0306406153"))
                .withMessageContaining("Invalid ISBN");
    }

    @Test
    void shouldThrowForInvalidIsbn13Checksum() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ISBN.of("9780132350885"))
                .withMessageContaining("Invalid ISBN");
    }

    @Test
    void shouldThrowForWrongLength() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ISBN.of("12345"));
    }

    @Test
    void shouldThrowForNonDigitCharactersInIsbn13() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ISBN.of("978A132350884"));
    }

    @Test
    void shouldThrowForNonDigitInFirstNinePositionsOfIsbn10() {
        // 'A' in position 0 triggers the isDigit guard in isIsbn10
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ISBN.of("A123456789"));
    }

    @Test
    void staticOfDelegatesToCanonicalConstructor() {
        assertThat(ISBN.of("9780132350884")).isInstanceOf(ISBN.class);
    }
}
