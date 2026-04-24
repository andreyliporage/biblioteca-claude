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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBookUseCaseTest {

    @Mock BookRepository bookRepository;
    @InjectMocks UpdateBookUseCase useCase;

    @Test
    void shouldUpdateBookAndReturnSaved() {
        Book book = new Book("LIV-00001", "Old Title", "Old Author", "9780132350884");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        UpdateBookUseCase.Input input = new UpdateBookUseCase.Input(1L, "New Title", "New Author", "9780201633610");
        Book result = useCase.execute(input);

        assertThat(result.getName()).isEqualTo("New Title");
        assertThat(result.getAuthor()).isEqualTo("New Author");
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        UpdateBookUseCase.Input input = new UpdateBookUseCase.Input(99L, "Title", "Author", "9780132350884");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> useCase.execute(input))
                .withMessageContaining("99");

        verify(bookRepository, never()).save(any());
    }
}
