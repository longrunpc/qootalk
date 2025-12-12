package com.lrchan.qootalk.domain.chat.vo;

public class FileName {
    
    private final String value;

    public FileName(String value) {
        validate(value);
        this.value = value;
    }
    
    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        if (value.length() < 2 || value.length() > 100) {
            throw new IllegalArgumentException("File name must be between 2 and 20 characters");
        }
        if (!value.matches("^[a-zA-Z0-9._%+-]+$")) {
            throw new IllegalArgumentException("File name must contain only letters, numbers, and special characters");
        }
    }
}
