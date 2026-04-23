package com.biblioteca.application.usecase.book;

import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchBooksUseCaseTest {

    @Mock BookRepository bookRepository;

    @InjectMocks SearchBooksUseCase useCase;

    @Test
    void shouldDelegateFiltersToRepository() {
        when(bookRepository.search(any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());

        useCase.execute(new SearchBooksUseCase.Input("Clean", null, null, null, BookStatus.AVAILABLE));

        verify(bookRepository).search("Clean", null, null, null, BookStatus.AVAILABLE);
    }

    @Test
    void shouldPassNullFiltersThrough() {
        when(bookRepository.search(null, null, null, null, null)).thenReturn(Collections.emptyList());

        useCase.execute(new SearchBooksUseCase.Input(null, null, null, null, null));

        verify(bookRepository).search(null, null, null, null, null);
    }
}
