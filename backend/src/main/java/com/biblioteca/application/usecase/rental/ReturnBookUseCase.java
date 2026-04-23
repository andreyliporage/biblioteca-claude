package com.biblioteca.application.usecase.rental;

import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnBookUseCase {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Rental execute(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.isActive()) {
            throw new IllegalStateException("Rental is already closed");
        }

        rental.returnBook();
        bookRepository.save(rental.getBook());
        return rentalRepository.save(rental);
    }
}
