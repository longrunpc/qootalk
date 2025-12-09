package com.lrchan.qootalk.domain.chat;

import java.time.LocalDateTime;
import java.util.*;

import com.lrchan.qootalk.domain.common.BaseModel;

public class Message extends BaseModel {

    private Long roomId;
    private Long userId;
    private String content;
    private MessageType messageType;
    private List<Long> mentions;
    private Long parentMessageId;

    private Message(Long id, Long roomId, Long userId, String content, MessageType messageType, List<Long> mentions,
            Long parentMessageId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.messageType = messageType;
        this.mentions = mentions;
        this.parentMessageId = parentMessageId;
    }

    public static Message create(Long roomId, Long userId, String content, MessageType messageType,
            List<Long> mentions) {
        return create(roomId, userId, content, messageType, mentions, null);
    }

    public static Message create(Long roomId, Long userId, String content, MessageType messageType,
            List<Long> mentions, Long parentMessageId) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return new Message(null, roomId, userId, content, messageType == null ? MessageType.TEXT : messageType,
                mentions == null ? Collections.emptyList() : new ArrayList<>(mentions), parentMessageId,
                LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Long roomId() {
        return roomId;
    }

    public Long userId() {
        return userId;
    }

    public String content() {
        return content;
    }

    public MessageType messageType() {
        return messageType;
    }

    public List<Long> mentions() {
        return mentions == null ? Collections.emptyList() : new ArrayList<>(mentions);
    }

    public Long parentMessageId() {
        return parentMessageId;
    }

    public void changeContent(String content) {
        this.content = content;
        update();
    }

    public void changeMessageType(MessageType messageType) {
        this.messageType = messageType == null ? MessageType.TEXT : messageType;
        update();
    }
    
    public void changeMentions(List<Long> userIds) {
        this.mentions = userIds == null ? Collections.emptyList() : new ArrayList<>(userIds);
        update();
    }
}
