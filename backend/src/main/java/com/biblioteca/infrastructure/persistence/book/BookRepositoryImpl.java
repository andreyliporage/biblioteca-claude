package com.biblioteca.infrastructure.persistence.book;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.domain.repository.BookRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository jpa;

    @Override
    public Book save(Book book) {
        return jpa.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Book> search(String name, String author, String isbn, String code, BookStatus status) {
        return jpa.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (author != null && !author.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
            }
            if (isbn != null && !isbn.isBlank()) {
                predicates.add(cb.like(root.get("isbn"), "%" + isbn + "%"));
            }
            if (code != null && !code.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
