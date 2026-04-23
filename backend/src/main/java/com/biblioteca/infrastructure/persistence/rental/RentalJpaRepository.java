package com.biblioteca.infrastructure.persistence.rental;

import com.biblioteca.domain.model.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface RentalJpaRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT r FROM Rental r JOIN FETCH r.book JOIN FETCH r.client WHERE r.id = :id")
    Optional<Rental> findByIdFetch(Long id);

    @Query("SELECT r FROM Rental r JOIN FETCH r.book JOIN FETCH r.client WHERE r.returnedAt IS NULL")
    List<Rental> findActive();

    @Query("SELECT r FROM Rental r JOIN FETCH r.book JOIN FETCH r.client WHERE (:bookId IS NULL OR r.book.id = :bookId) AND (:clientId IS NULL OR r.client.id = :clientId)")
    List<Rental> findHistory(Long bookId, Long clientId);
}
