package com.biblioteca.infrastructure.persistence.rental;

import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RentalRepositoryImpl implements RentalRepository {

    private final RentalJpaRepository jpa;

    @Override
    public Rental save(Rental rental) {
        return jpa.save(rental);
    }

    @Override
    public Optional<Rental> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Rental> findActive() {
        return jpa.findActive();
    }

    @Override
    public List<Rental> findHistory(Long bookId, Long clientId) {
        return jpa.findHistory(bookId, clientId);
    }
}
