package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.rental.CreateRentalUseCase;
import com.biblioteca.application.usecase.rental.ReturnBookUseCase;
import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.RentalRepository;
import com.biblioteca.infrastructure.security.JwtTokenProvider;
import com.biblioteca.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
@Import(SecurityConfig.class)
class RentalControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CreateRentalUseCase createRentalUseCase;
    @MockBean ReturnBookUseCase returnBookUseCase;
    @MockBean RentalRepository rentalRepository;
    @MockBean JwtTokenProvider jwtTokenProvider;

    private static final LocalDate TODAY = LocalDate.of(2026, 4, 24);

    private Rental buildRental() {
        Book book = new Book("LIV-00001", "Clean Code", "Author", "9780132350884");
        Client client = new Client("Ana Lima", "ana@email.com", "11999990000");
        return new Rental(book, client, TODAY, TODAY.plusDays(7));
    }

    @Test
    @WithMockUser
    void create_returns201WithRental() throws Exception {
        when(createRentalUseCase.execute(any())).thenReturn(buildRental());

        mockMvc.perform(post("/api/rentals").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookId":1,"clientId":1,"startDate":"2026-04-24","endDate":"2026-05-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookName").value("Clean Code"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void create_returns400WhenRequiredFieldsMissing() throws Exception {
        mockMvc.perform(post("/api/rentals").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(createRentalUseCase, never()).execute(any());
    }

    @Test
    @WithMockUser
    void returnBook_returns200WithUpdatedRental() throws Exception {
        Rental returned = buildRental();
        returned.returnBook();
        when(returnBookUseCase.execute(1L)).thenReturn(returned);

        mockMvc.perform(put("/api/rentals/1/return").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser
    void listActive_returnsActiveRentals() throws Exception {
        when(rentalRepository.findActive()).thenReturn(List.of(buildRental()));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookCode").value("LIV-00001"));
    }

    @Test
    @WithMockUser
    void history_returnsAllRentalsFilteredByBookAndClient() throws Exception {
        when(rentalRepository.findHistory(1L, 2L)).thenReturn(List.of(buildRental()));

        mockMvc.perform(get("/api/rentals/history")
                        .param("bookId", "1")
                        .param("clientId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName").value("Ana Lima"));
    }

    @Test
    @WithMockUser
    void history_returnsAllRentalsWithNullFilters() throws Exception {
        when(rentalRepository.findHistory(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/rentals/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
