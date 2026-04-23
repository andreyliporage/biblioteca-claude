package com.biblioteca.application.usecase.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.RentalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnBookUseCaseTest {

    @Mock RentalRepository rentalRepository;
    @Mock BookRepository bookRepository;

    @InjectMocks ReturnBookUseCase useCase;

    private Rental activeRental() {
        var book = new Book("LIV-00001", "Clean Code", "Robert Martin", "9780132350884");
        var client = new Client("John Doe", "john@example.com", null);
        return new Rental(book, client, LocalDate.now(), LocalDate.now().plusDays(7));
    }

    @Test
    void shouldReturnBookAndChangeStatusToAvailable() {
        var rental = activeRental();
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = useCase.execute(1L);

        assertThat(result.isActive()).isFalse();
        assertThat(result.getReturnedAt()).isNotNull();
        assertThat(result.getBook().getStatus()).isEqualTo(BookStatus.AVAILABLE);
        verify(bookRepository).save(rental.getBook());
        verify(rentalRepository).save(rental);
    }

    @Test
    void shouldThrowWhenRentalNotFound() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rental not found");
    }

    @Test
    void shouldThrowWhenRentalAlreadyClosed() {
        var rental = activeRental();
        rental.returnBook();

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        assertThatThrownBy(() -> useCase.execute(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Rental is already closed");
    }
}
