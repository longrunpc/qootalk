package com.lrchan.qootalk.domain.chat;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class MessageReply extends BaseModel {

    private Long messageId;
    private Long parentMessageId;

    private MessageReply(Long id, Long messageId, Long parentMessageId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.messageId = messageId;
        this.parentMessageId = parentMessageId;
    }

    public static MessageReply create(Long messageId, Long parentMessageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
        if (parentMessageId == null) {
            throw new IllegalArgumentException("Parent message ID cannot be null");
        }
        if (messageId.equals(parentMessageId)) {
            throw new IllegalArgumentException("Message ID and parent message ID cannot be the same");
        }
        return new MessageReply(null, messageId, parentMessageId, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Long messageId() {
        return messageId;
    }

    public Long parentMessageId() {
        return parentMessageId;
    }
}
