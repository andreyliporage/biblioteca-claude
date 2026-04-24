package com.biblioteca.infrastructure.security;

import com.biblioteca.domain.model.admin.Admin;
import com.biblioteca.domain.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPasswordInitializerTest {

    @Mock AdminRepository adminRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AdminPasswordInitializer initializer;

    @Test
    void run_skipsCreationWhenAdminAlreadyExists() throws Exception {
        when(adminRepository.existsAny()).thenReturn(true);

        initializer.run();

        verify(adminRepository, never()).save(any());
    }

    @Test
    void run_createsAdminWithEncodedPasswordWhenNoneExists() throws Exception {
        when(adminRepository.existsAny()).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(adminRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        initializer.run();

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());
        Admin saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("admin");
        assertThat(saved.getPassword()).isEqualTo("encoded_password");
    }

    @Test
    void run_generatesPasswordWithCorrectLength() throws Exception {
        when(adminRepository.existsAny()).thenReturn(false);
        ArgumentCaptor<String> rawPasswordCaptor = ArgumentCaptor.forClass(String.class);
        when(passwordEncoder.encode(rawPasswordCaptor.capture())).thenReturn("encoded");
        when(adminRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        initializer.run();

        assertThat(rawPasswordCaptor.getValue()).hasSize(16);
    }
}
