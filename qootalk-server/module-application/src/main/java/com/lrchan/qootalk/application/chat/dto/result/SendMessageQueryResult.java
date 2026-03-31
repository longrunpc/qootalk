package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

public record SendMessageQueryResult(
    Long id,
    Long roomId,
    Long senderId,
    String content,
    MessageType messageType,
    List<Long> mentions,
    Long parentMessageId,
    List<Long> attachmentIds,
    LocalDateTime createdAt
) {
    public static SendMessageQueryResult of(Message message, List<Long> attachmentIds) {
        return new SendMessageQueryResult(
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
