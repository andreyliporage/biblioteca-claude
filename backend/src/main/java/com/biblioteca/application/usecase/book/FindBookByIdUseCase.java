package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindBookByIdUseCase {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public Optional<Book> execute(Long id) {
        return bookRepository.findById(id);
    }
}
