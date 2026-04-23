package com.biblioteca.presentation.dto;

import com.biblioteca.domain.model.book.Book;

public record BookResponse(Long id, String code, String name, String author, String isbn, String status) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(), book.getCode(), book.getName(),
                book.getAuthor(), book.getIsbn(), book.getStatus().name()
        );
    }
}
