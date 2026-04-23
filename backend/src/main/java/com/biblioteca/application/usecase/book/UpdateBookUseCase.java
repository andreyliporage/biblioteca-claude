package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateBookUseCase {

    private final BookRepository bookRepository;

    public record Input(Long id, String name, String author, String isbn) {}

    @Transactional
    public Book execute(Input input) {
        Book book = bookRepository.findById(input.id())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + input.id()));
        book.update(input.name(), input.author(), input.isbn());
        return bookRepository.save(book);
    }
}
