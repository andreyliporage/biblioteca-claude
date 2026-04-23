package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.rental.CreateRentalUseCase;
import com.biblioteca.application.usecase.rental.ReturnBookUseCase;
import com.biblioteca.domain.repository.RentalRepository;
import com.biblioteca.presentation.dto.RentalRequest;
import com.biblioteca.presentation.dto.RentalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final CreateRentalUseCase createRentalUseCase;
    private final ReturnBookUseCase returnBookUseCase;
    private final RentalRepository rentalRepository;

    @PostMapping
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody RentalRequest request) {
        var rental = createRentalUseCase.execute(
                new CreateRentalUseCase.Input(request.bookId(), request.clientId(), request.startDate(), request.endDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(RentalResponse.from(rental));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<RentalResponse> returnBook(@PathVariable Long id) {
        var rental = returnBookUseCase.execute(id);
        return ResponseEntity.ok(RentalResponse.from(rental));
    }

    @GetMapping
    public ResponseEntity<List<RentalResponse>> listActive() {
        var rentals = rentalRepository.findActive();
        return ResponseEntity.ok(rentals.stream().map(RentalResponse::from).toList());
    }

    @GetMapping("/history")
    public ResponseEntity<List<RentalResponse>> history(
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long clientId) {
        var rentals = rentalRepository.findHistory(bookId, clientId);
        return ResponseEntity.ok(rentals.stream().map(RentalResponse::from).toList());
    }
}
