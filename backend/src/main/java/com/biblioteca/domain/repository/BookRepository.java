package com.biblioteca.domain.repository;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> search(String name, String author, String isbn, String code, BookStatus status);
    long count();
}
