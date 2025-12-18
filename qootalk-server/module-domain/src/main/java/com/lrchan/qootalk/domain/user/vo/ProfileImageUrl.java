package com.lrchan.qootalk.domain.user.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

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
            throw new DomainException(UserErrorCode.USER_INVALID_PROFILE_IMAGE_URL);
        }
        if (!value.matches("^https?://.*")) {
            throw new DomainException(UserErrorCode.USER_INVALID_PROFILE_IMAGE_URL);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileImageUrl)) return false;
        ProfileImageUrl profileImageUrl = (ProfileImageUrl) o;
        return value.equals(profileImageUrl.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
