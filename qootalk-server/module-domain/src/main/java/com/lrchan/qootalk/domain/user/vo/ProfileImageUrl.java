package com.lrchan.qootalk.domain.user.vo;

public class ProfileImageUrl {
    
    private final String value;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileImageUrl that = (ProfileImageUrl) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
