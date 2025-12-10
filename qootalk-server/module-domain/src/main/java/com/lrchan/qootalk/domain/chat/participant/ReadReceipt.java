package com.lrchan.qootalk.domain.chat.participant;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class ReadReceipt extends BaseModel {
    
    private final Long userId;
    private final Long roomId;
    private Long lastReadMessageId;

    private ReadReceipt(Long id, Long userId, Long roomId, Long lastReadMessageId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.userId = userId;
        this.roomId = roomId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public static ReadReceipt create(Long userId, Long roomId, Long lastReadMessageId) {
        return new ReadReceipt(null, userId, roomId, lastReadMessageId, LocalDateTime.now(), LocalDateTime.now(), null);
    }
    
}
