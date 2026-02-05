package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import com.lrchan.qootalk.domain.chat.message.Message;

public class MessageEntityMapper {
    
    private MessageEntityMapper() {
    }

    public static MessageEntity toEntity(Message message) {
        return MessageEntity.builder()
            .id(message.id())
            .roomId(message.roomId())
            .userId(message.userId())
            .content(message.content())
            .messageType(message.messageType())
            .mentions(message.mentions())
            .parentMessageId(message.parentMessageId())
            .createdAt(message.createdAt())
            .updatedAt(message.updatedAt())
            .deletedAt(message.deletedAt())
            .build();
    }

    public static Message toDomain(MessageEntity messageEntity) {
        return Message.reconstruct(messageEntity.getId(), messageEntity.getRoomId(), messageEntity.getUserId(), messageEntity.getContent(), messageEntity.getMessageType(), messageEntity.getMentions(), messageEntity.getParentMessageId(), messageEntity.getCreatedAt(), messageEntity.getUpdatedAt(), messageEntity.getDeletedAt());
    }
}
