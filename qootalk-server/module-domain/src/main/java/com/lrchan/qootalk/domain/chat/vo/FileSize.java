package com.lrchan.qootalk.domain.chat.vo;

public class FileSize {

    private static final long GLOBAL_MAX = 500L * 1024 * 1024;
    
    private final Long value;

    public FileSize(Long value) {
        validate(value);
        this.value = value;
    }
    
    public Long value() {
        return value;
    }

    private void validate(Long value) {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("File size cannot be null or negative");
        }
        if (value > GLOBAL_MAX) {
            throw new IllegalArgumentException("File size cannot be greater than " + GLOBAL_MAX + " bytes");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSize fileSize = (FileSize) o;
        return value.equals(fileSize.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
