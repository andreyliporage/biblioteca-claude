package com.biblioteca.application.usecase.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.ClientRepository;
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
class CreateRentalUseCaseTest {

    @Mock RentalRepository rentalRepository;
    @Mock BookRepository bookRepository;
    @Mock ClientRepository clientRepository;

    @InjectMocks CreateRentalUseCase useCase;

    private Book availableBook() {
        return new Book("LIV-00001", "Clean Code", "Robert Martin", "9780132350884");
    }

    private Client client() {
        return new Client("John Doe", "john@example.com", "99999-0000");
    }

    @Test
    void shouldCreateRentalAndChangeBookStatusToRented() {
        var book = availableBook();
        var client = client();
        var start = LocalDate.now();
        var end = start.plusDays(7);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rental result = useCase.execute(new CreateRentalUseCase.Input(1L, 1L, start, end));

        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getClient()).isEqualTo(client);
        assertThat(result.isActive()).isTrue();
        assertThat(book.getStatus()).isEqualTo(BookStatus.RENTED);
        verify(bookRepository).save(book);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new CreateRentalUseCase.Input(99L, 1L, LocalDate.now(), LocalDate.now().plusDays(7))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");
    }

    @Test
    void shouldThrowWhenClientNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook()));
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new CreateRentalUseCase.Input(1L, 99L, LocalDate.now(), LocalDate.now().plusDays(7))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client not found");
    }

    @Test
    void shouldThrowWhenBookIsNotAvailable() {
        var book = availableBook();
        book.rent();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));

        assertThatThrownBy(() ->
                useCase.execute(new CreateRentalUseCase.Input(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(7))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Book is not available for rental");
    }

    @Test
    void shouldThrowWhenRentalPeriodIsBelowMinimum() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook()));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client()));

        var start = LocalDate.now();
        var end = start.plusDays(3);

        assertThatThrownBy(() ->
                useCase.execute(new CreateRentalUseCase.Input(1L, 1L, start, end)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least");
    }
}
