package com.biblioteca.domain.model.rental;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "rental")
@Getter
@NoArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "returned_at")
    private LocalDate returnedAt;

    public Rental(Book book, Client client, LocalDate startDate, LocalDate endDate) {
        RentalPeriod period = new RentalPeriod(startDate, endDate);
        book.rent();
        this.book = book;
        this.client = client;
        this.startDate = period.startDate();
        this.endDate = period.endDate();
    }

    public void returnBook() {
        this.returnedAt = LocalDate.now();
        this.book.returnBook();
    }

    public boolean isActive() {
        return returnedAt == null;
    }
}
