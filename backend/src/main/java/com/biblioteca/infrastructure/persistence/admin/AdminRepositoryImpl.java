package com.biblioteca.infrastructure.persistence.admin;

import com.biblioteca.domain.model.admin.Admin;
import com.biblioteca.domain.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminJpaRepository jpa;

    @Override
    public Admin save(Admin admin) {
        return jpa.save(admin);
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        return jpa.findByUsername(username);
    }

    @Override
    public boolean existsAny() {
        return jpa.count() > 0;
    }
}
