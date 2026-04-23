package com.biblioteca.domain.repository;

import com.biblioteca.domain.model.admin.Admin;

import java.util.Optional;

public interface AdminRepository {
    Admin save(Admin admin);
    Optional<Admin> findByUsername(String username);
    boolean existsAny();
}
