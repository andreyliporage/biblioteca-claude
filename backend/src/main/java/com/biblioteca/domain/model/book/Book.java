package com.biblioteca.domain.model.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    public Book(String code, String name, String author, String isbn) {
        this.code = code;
        this.name = name;
        this.author = author;
        this.isbn = ISBN.of(isbn).value();
        this.status = BookStatus.AVAILABLE;
    }

    public void rent() {
        if (this.status != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available for rental");
        }
        this.status = BookStatus.RENTED;
    }

    public void returnBook() {
        this.status = BookStatus.AVAILABLE;
    }

    public void update(String name, String author, String isbn) {
        this.name = name;
        this.author = author;
        this.isbn = ISBN.of(isbn).value();
    }
}
