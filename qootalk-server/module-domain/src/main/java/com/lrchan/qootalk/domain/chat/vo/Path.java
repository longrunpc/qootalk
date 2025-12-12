package com.lrchan.qootalk.domain.chat.vo;

public class Path {
    private final String value;
    
    public Path(String value) {
        validate(value);
        this.value = value;
    }
    
    public String value() {
        return value;
    }
    
    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or blank");
        }
        if (value.length() < 2 || value.length() > 200) {
            throw new IllegalArgumentException("Path must be between 2 and 200 characters");
        }
        if (!value.matches("^[a-zA-Z0-9._%+-]+$")) {
            throw new IllegalArgumentException("Path must contain only letters, numbers, and special characters");
        }
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with a slash");
        }
        if (!value.endsWith("/")) {
            throw new IllegalArgumentException("Path must end with a slash");
        }
        if (value.contains("//")) {
            throw new IllegalArgumentException("Path cannot contain multiple slashes");
        }
        if (value.contains("..")) {
            throw new IllegalArgumentException("Path cannot contain parent directory");
        }
    }
}
