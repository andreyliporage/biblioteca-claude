package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterBookUseCaseTest {

    @Mock BookRepository bookRepository;

    @InjectMocks RegisterBookUseCase useCase;

    @Test
    void shouldRegisterBookWithAvailableStatusAndGeneratedCode() {
        when(bookRepository.count()).thenReturn(0L);
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = useCase.execute(new RegisterBookUseCase.Input("Clean Code", "Robert Martin", "9780132350884"));

        assertThat(result.getCode()).isEqualTo("LIV-00001");
        assertThat(result.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(result.getName()).isEqualTo("Clean Code");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldIncrementCodeBasedOnCurrentCount() {
        when(bookRepository.count()).thenReturn(4L);
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = useCase.execute(new RegisterBookUseCase.Input("Effective Java", "Joshua Bloch", "9780134685991"));

        assertThat(result.getCode()).isEqualTo("LIV-00005");
    }

    @Test
    void shouldThrowWhenIsbnIsInvalid() {
        when(bookRepository.count()).thenReturn(0L);

        assertThatThrownBy(() ->
                useCase.execute(new RegisterBookUseCase.Input("Some Book", "Author", "invalid-isbn")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ISBN");
    }
}
