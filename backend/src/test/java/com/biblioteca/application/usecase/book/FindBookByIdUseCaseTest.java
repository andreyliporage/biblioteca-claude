package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindBookByIdUseCaseTest {

    @Mock BookRepository bookRepository;
    @InjectMocks FindBookByIdUseCase useCase;

    @Test
    void shouldReturnBookWhenFound() {
        Book book = new Book("LIV-00001", "Clean Code", "Author", "9780132350884");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = useCase.execute(1L);

        assertThat(result).isPresent().contains(book);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Book> result = useCase.execute(99L);

        assertThat(result).isEmpty();
    }
}
