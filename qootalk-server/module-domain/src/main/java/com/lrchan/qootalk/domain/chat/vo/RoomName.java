package com.lrchan.qootalk.domain.chat.vo;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public class RoomName {
    
    private final String value;

    public RoomName(String value) {
        validate(value);
        this.value = value;
    }
    
    public String value() {
        return value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_INVALID_NAME);
        }
        if (value.length() < 2 || value.length() > 20) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_INVALID_NAME);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomName roomName = (RoomName) o;
        return java.util.Objects.equals(value, roomName.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
