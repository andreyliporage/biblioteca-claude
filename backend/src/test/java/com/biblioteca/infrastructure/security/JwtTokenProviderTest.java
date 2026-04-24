package com.biblioteca.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "QmlibGlvdGVjYUFwcFNlY3JldEtleTIwMjQhISEhISE=";
    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, 86400000L);
    }

    @Test
    void generateToken_returnsNonBlankToken() {
        String token = provider.generateToken("admin");
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectSubject() {
        String token = provider.generateToken("admin");
        assertThat(provider.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    void isValid_returnsTrueForFreshToken() {
        String token = provider.generateToken("admin");
        assertThat(provider.isValid(token)).isTrue();
    }

    @Test
    void isValid_returnsFalseForTamperedToken() {
        String token = provider.generateToken("admin");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(provider.isValid(tampered)).isFalse();
    }

    @Test
    void isValid_returnsFalseForExpiredToken() {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, -1L);
        String expired = shortLived.generateToken("admin");
        assertThat(provider.isValid(expired)).isFalse();
    }

    @Test
    void isValid_returnsFalseForGarbage() {
        assertThat(provider.isValid("not.a.jwt")).isFalse();
    }
}
