package com.biblioteca.infrastructure.persistence.rental;

import com.biblioteca.domain.model.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface RentalJpaRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT r FROM Rental r WHERE r.returnedAt IS NULL")
    List<Rental> findActive();

    @Query("SELECT r FROM Rental r WHERE (:bookId IS NULL OR r.book.id = :bookId) AND (:clientId IS NULL OR r.client.id = :clientId)")
    List<Rental> findHistory(Long bookId, Long clientId);
}
