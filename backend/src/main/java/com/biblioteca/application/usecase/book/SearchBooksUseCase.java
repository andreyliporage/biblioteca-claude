package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchBooksUseCase {

    private final BookRepository bookRepository;

    public record Input(String name, String author, String isbn, String code, BookStatus status) {}

    @Transactional(readOnly = true)
    public List<Book> execute(Input input) {
        return bookRepository.search(input.name(), input.author(), input.isbn(), input.code(), input.status());
    }
}
