package com.lrchan.qootalk.domain.chat.vo;

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
            throw new IllegalArgumentException("Room name cannot be null or blank");
        }
        if (value.length() < 2 || value.length() > 20) {
            throw new IllegalArgumentException("Room name must be between 2 and 20 characters");
        }
    }
}
