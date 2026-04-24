package com.biblioteca.infrastructure.seed;

import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.ClientRepository;
import com.biblioteca.domain.repository.RentalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock BookRepository bookRepository;
    @Mock ClientRepository clientRepository;
    @Mock RentalRepository rentalRepository;
    @InjectMocks DataSeeder seeder;

    @Test
    void run_skipsWhenDataAlreadyExists() throws Exception {
        when(bookRepository.count()).thenReturn(1L);

        seeder.run();

        verify(bookRepository, never()).save(any());
        verify(clientRepository, never()).save(any());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void run_seedsAllEntitiesWhenDatabaseIsEmpty() throws Exception {
        when(bookRepository.count()).thenReturn(0L);
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        seeder.run();

        // 25 first names × 8 last names = 200 clients
        verify(clientRepository, times(200)).save(any());
        // 50 titles × 4 copies = 200 books
        verify(bookRepository, times(200)).save(any());
        // 100 historical + 60 active = 160 rentals
        verify(rentalRepository, times(160)).save(any());
    }
}
