package com.lrchan.qootalk.domain.user.vo;

public class ProfileImageUrl {
    
    private String value;

    public ProfileImageUrl(String value) {
        validate(value);
        this.value = value;
    }

    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Profile image URL cannot be null or blank");
        }
        if (!value.matches("^https?://.*")) {
            throw new IllegalArgumentException("Invalid profile image URL format");
        }
    }
}
