package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.book.*;
import com.biblioteca.domain.model.book.BookStatus;
import com.biblioteca.presentation.dto.BookRequest;
import com.biblioteca.presentation.dto.BookResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final RegisterBookUseCase registerBookUseCase;
    private final SearchBooksUseCase searchBooksUseCase;
    private final FindBookByIdUseCase findBookByIdUseCase;
    private final UpdateBookUseCase updateBookUseCase;

    @PostMapping
    public ResponseEntity<BookResponse> register(@Valid @RequestBody BookRequest request) {
        var book = registerBookUseCase.execute(
                new RegisterBookUseCase.Input(request.name(), request.author(), request.isbn()));
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) BookStatus status) {
        var books = searchBooksUseCase.execute(
                new SearchBooksUseCase.Input(name, author, isbn, code, status));
        return ResponseEntity.ok(books.stream().map(BookResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
        return findBookByIdUseCase.execute(id)
                .map(BookResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        var book = updateBookUseCase.execute(
                new UpdateBookUseCase.Input(id, request.name(), request.author(), request.isbn()));
        return ResponseEntity.ok(BookResponse.from(book));
    }
}
