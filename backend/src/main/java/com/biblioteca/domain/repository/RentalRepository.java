package com.biblioteca.domain.repository;

import com.biblioteca.domain.model.rental.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findById(Long id);
    List<Rental> findActive();
    List<Rental> findHistory(Long bookId, Long clientId);
}
