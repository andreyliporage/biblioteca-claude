package com.biblioteca.domain.model.book;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BookTest {

    private static final String VALID_ISBN = "9780132350884";

    @Test
    void constructor_setsAllFieldsAndDefaultsToAvailable() {
        Book book = new Book("LIV-00001", "Clean Code", "Robert C. Martin", VALID_ISBN);
        assertThat(book.getCode()).isEqualTo("LIV-00001");
        assertThat(book.getName()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(book.getIsbn()).isEqualTo(VALID_ISBN);
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void constructor_throwsWhenIsbnIsInvalid() {
        // 1234567890: sum=210, 210%11=1 ≠ 0 → invalid
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Book("LIV-00001", "Title", "Author", "1234567890"));
    }

    @Test
    void rent_changesStatusToRented() {
        Book book = new Book("LIV-00001", "Title", "Author", VALID_ISBN);
        book.rent();
        assertThat(book.getStatus()).isEqualTo(BookStatus.RENTED);
    }

    @Test
    void rent_throwsWhenBookIsNotAvailable() {
        Book book = new Book("LIV-00001", "Title", "Author", VALID_ISBN);
        book.rent();
        assertThatIllegalStateException()
                .isThrownBy(book::rent)
                .withMessageContaining("not available");
    }

    @Test
    void returnBook_changesStatusBackToAvailable() {
        Book book = new Book("LIV-00001", "Title", "Author", VALID_ISBN);
        book.rent();
        book.returnBook();
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void update_updatesNameAuthorAndIsbn() {
        Book book = new Book("LIV-00001", "Old Title", "Old Author", VALID_ISBN);
        String newIsbn = "9780201633610";
        book.update("New Title", "New Author", newIsbn);
        assertThat(book.getName()).isEqualTo("New Title");
        assertThat(book.getAuthor()).isEqualTo("New Author");
        assertThat(book.getIsbn()).isEqualTo(newIsbn);
    }

    @Test
    void update_throwsWhenNewIsbnIsInvalid() {
        Book book = new Book("LIV-00001", "Title", "Author", VALID_ISBN);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> book.update("Title", "Author", "1234567890"));
    }

    @Test
    void bookStatusValuesAreAccessible() {
        assertThat(BookStatus.values()).containsExactly(
                BookStatus.AVAILABLE, BookStatus.RENTED, BookStatus.MAINTENANCE);
        assertThat(BookStatus.valueOf("AVAILABLE")).isEqualTo(BookStatus.AVAILABLE);
    }
}
