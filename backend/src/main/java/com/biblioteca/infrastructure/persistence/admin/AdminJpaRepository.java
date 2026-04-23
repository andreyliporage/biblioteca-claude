package com.biblioteca.infrastructure.persistence.admin;

import com.biblioteca.domain.model.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface AdminJpaRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
