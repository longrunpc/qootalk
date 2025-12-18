package com.lrchan.qootalk.domain.user.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

public class Password {
    
    private String encryptedPassword;

    public Password(String encryptedPassword) {
        validate(encryptedPassword);
        this.encryptedPassword = encryptedPassword;
    }

    public String encryptedPassword() {
        return encryptedPassword;
    }

    private void validate(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new DomainException(UserErrorCode.USER_INVALID_PASSWORD);
        }
    }
}
