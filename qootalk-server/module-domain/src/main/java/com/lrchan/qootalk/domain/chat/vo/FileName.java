package com.lrchan.qootalk.domain.chat.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

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
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME);
        }
        if (value.length() < 2 || value.length() > 100) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME);
        }
        if (!value.matches("^[a-zA-Z0-9._%+-]+$")) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_METADATA_INVALID_FILE_NAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileName fileName = (FileName) o;
        return value.equals(fileName.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
