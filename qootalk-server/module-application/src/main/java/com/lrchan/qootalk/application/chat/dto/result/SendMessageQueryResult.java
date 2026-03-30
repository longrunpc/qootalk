package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

import lombok.Builder;

@Builder 
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
        return SendMessageQueryResult.builder()
            .id(message.id())
            .roomId(message.roomId())
            .senderId(message.userId())
            .content(message.content())
            .messageType(message.messageType())
            .mentions(message.mentions())
            .parentMessageId(message.parentMessageId())
            .attachmentIds(attachmentIds)
            .createdAt(message.createdAt())
            .build();
    }
}
