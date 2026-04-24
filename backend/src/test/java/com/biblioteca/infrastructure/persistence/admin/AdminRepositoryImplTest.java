package com.biblioteca.infrastructure.persistence.admin;

import com.biblioteca.domain.model.admin.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(AdminRepositoryImpl.class)
class AdminRepositoryImplTest {

    @Autowired AdminRepositoryImpl adminRepository;

    @Test
    void save_persistsAdmin() {
        Admin saved = adminRepository.save(new Admin("admin", "encoded"));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("admin");
    }

    @Test
    void findByUsername_returnsAdminWhenExists() {
        adminRepository.save(new Admin("admin", "encoded"));
        Optional<Admin> result = adminRepository.findByUsername("admin");
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void findByUsername_returnsEmptyWhenNotExists() {
        Optional<Admin> result = adminRepository.findByUsername("ghost");
        assertThat(result).isEmpty();
    }

    @Test
    void existsAny_returnsFalseWhenEmpty() {
        assertThat(adminRepository.existsAny()).isFalse();
    }

    @Test
    void existsAny_returnsTrueAfterSave() {
        adminRepository.save(new Admin("admin", "encoded"));
        assertThat(adminRepository.existsAny()).isTrue();
    }
}
