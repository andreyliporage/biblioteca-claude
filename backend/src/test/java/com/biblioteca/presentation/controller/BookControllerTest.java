package com.biblioteca.presentation.controller;

import com.biblioteca.application.usecase.book.*;
import com.biblioteca.domain.model.book.Book;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean RegisterBookUseCase registerBookUseCase;
    @MockBean SearchBooksUseCase searchBooksUseCase;
    @MockBean FindBookByIdUseCase findBookByIdUseCase;
    @MockBean UpdateBookUseCase updateBookUseCase;
    @MockBean JwtTokenProvider jwtTokenProvider;

    private static final Book BOOK = new Book("LIV-00001", "Clean Code", "Robert C. Martin", "9780132350884");

    @Test
    @WithMockUser
    void register_returns201WithBook() throws Exception {
        when(registerBookUseCase.execute(any())).thenReturn(BOOK);

        mockMvc.perform(post("/api/books").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Clean Code","author":"Robert C. Martin","isbn":"9780132350884"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("LIV-00001"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    @WithMockUser
    void register_returns400WhenFieldsAreBlank() throws Exception {
        mockMvc.perform(post("/api/books").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","author":"","isbn":""}
                                """))
                .andExpect(status().isBadRequest());

        verify(registerBookUseCase, never()).execute(any());
    }

    @Test
    @WithMockUser
    void search_returnsListOfBooks() throws Exception {
        when(searchBooksUseCase.execute(any())).thenReturn(List.of(BOOK));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("LIV-00001"));
    }

    @Test
    @WithMockUser
    void findById_returns200WhenFound() throws Exception {
        when(findBookByIdUseCase.execute(1L)).thenReturn(Optional.of(BOOK));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clean Code"));
    }

    @Test
    @WithMockUser
    void findById_returns404WhenNotFound() throws Exception {
        when(findBookByIdUseCase.execute(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_returns200WithUpdatedBook() throws Exception {
        Book updated = new Book("LIV-00001", "New Title", "New Author", "9780201633610");
        when(updateBookUseCase.execute(any())).thenReturn(updated);

        mockMvc.perform(put("/api/books/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Title","author":"New Author","isbn":"9780201633610"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Title"));
    }

    @Test
    void search_returns403WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());
    }
}
