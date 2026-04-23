package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterBookUseCase {

    private final BookRepository bookRepository;

    public record Input(String name, String author, String isbn) {}

    @Transactional
    public Book execute(Input input) {
        long count = bookRepository.count();
        String code = String.format("LIV-%05d", count + 1);
        return bookRepository.save(new Book(code, input.name(), input.author(), input.isbn()));
    }
}
