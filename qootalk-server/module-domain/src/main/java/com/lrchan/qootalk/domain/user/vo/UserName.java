package com.lrchan.qootalk.domain.user.vo;

public class UserName {
    
    private String value;

    public UserName(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (value.length() < 2 || value.length() > 20) {
            throw new IllegalArgumentException("Username must be between 2 and 20 characters");
        }
    }
}
