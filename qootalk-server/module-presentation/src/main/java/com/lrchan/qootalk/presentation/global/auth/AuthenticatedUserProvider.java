package com.lrchan.qootalk.presentation.global.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.common.error.GlobalErrorCode;
import com.lrchan.qootalk.common.exception.ApplicationException;

@Component
public class AuthenticatedUserProvider {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ApplicationException(GlobalErrorCode.UNAUTHORIZED);
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new ApplicationException(GlobalErrorCode.UNAUTHORIZED, e);
        }
    }
}
