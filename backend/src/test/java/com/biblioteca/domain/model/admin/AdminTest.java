package com.biblioteca.domain.model.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AdminTest {

    @Test
    void constructor_setsUsernameAndPassword() {
        Admin admin = new Admin("admin", "encoded_password");
        assertThat(admin.getUsername()).isEqualTo("admin");
        assertThat(admin.getPassword()).isEqualTo("encoded_password");
        assertThat(admin.getId()).isNull();
    }
}
