package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import com.lrchan.qootalk.domain.chat.message.Message;

public class MessageEntityMapper {
    
    private MessageEntityMapper() {
    }

    public static MessageEntity toEntity(Message message) {
        return new MessageEntity(message.id(), message.roomId(), message.userId(), message.content(), message.messageType(), message.mentions(), message.parentMessageId(), message.createdAt(), message.updatedAt(), message.deletedAt());
    }

    public static Message toDomain(MessageEntity messageEntity) {
        return Message.reconstruct(messageEntity.id(), messageEntity.roomId(), messageEntity.userId(), messageEntity.content(), messageEntity.messageType(), messageEntity.mentions(), messageEntity.parentMessageId(), messageEntity.createdAt(), messageEntity.updatedAt(), messageEntity.deletedAt());
    }
}
