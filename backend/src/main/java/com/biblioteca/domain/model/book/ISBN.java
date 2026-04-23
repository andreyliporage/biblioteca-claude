package com.biblioteca.domain.model.book;

public record ISBN(String value) {

    public ISBN {
        String cleaned = value.replaceAll("[\\s-]", "");
        if (!isIsbn10(cleaned) && !isIsbn13(cleaned)) {
            throw new IllegalArgumentException("Invalid ISBN: " + value);
        }
        value = cleaned;
    }

    public static ISBN of(String raw) {
        return new ISBN(raw);
    }

    private static boolean isIsbn10(String isbn) {
        if (isbn.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(isbn.charAt(i))) return false;
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        char last = isbn.charAt(9);
        sum += (last == 'X' || last == 'x') ? 10 : (last - '0');
        return sum % 11 == 0;
    }

    private static boolean isIsbn13(String isbn) {
        if (isbn.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(isbn.charAt(i))) return false;
            sum += (isbn.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }
}
