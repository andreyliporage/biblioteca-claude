package com.biblioteca.application.usecase.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.ClientRepository;
import com.biblioteca.domain.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateRentalUseCase {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;

    public record Input(Long bookId, Long clientId, LocalDate startDate, LocalDate endDate) {}

    @Transactional
    public Rental execute(Input input) {
        Book book = bookRepository.findById(input.bookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Client client = clientRepository.findById(input.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Rental rental = new Rental(book, client, input.startDate(), input.endDate());
        bookRepository.save(book);
        return rentalRepository.save(rental);
    }
}
