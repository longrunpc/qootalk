package com.lrchan.qootalk.application.chat.dto.event;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

public record ChatMessageEvent(
    Long messageId,
    Long roomId,
    Long senderId,
    String content,
    MessageType messageType,
    List<Long> mentions,
    Long parentMessageId,
    List<Long> attachmentIds,
    LocalDateTime createdAt
) {
    public static ChatMessageEvent of(Message message, List<Long> attachmentIds) {
        return new ChatMessageEvent(
            message.id(),
            message.roomId(),
            message.userId(),
            message.content(),
            message.messageType(),
            message.mentions(),
            message.parentMessageId(),
            attachmentIds,
            message.createdAt()
        );
    }
}
