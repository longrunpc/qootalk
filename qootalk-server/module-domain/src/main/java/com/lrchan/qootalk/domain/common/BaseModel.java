package com.lrchan.qootalk.domain.common;

import java.time.LocalDateTime;

public abstract class BaseModel {
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected LocalDateTime deletedAt;

    protected BaseModel(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
        this.updatedAt = (updatedAt == null) ? LocalDateTime.now() : updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long id() {
        return id;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update() {
        this.updatedAt = LocalDateTime.now();
    }
}
