package com.lrchan.qootalk.domain.chat.message;

import java.time.LocalDateTime;
import java.util.*;

import com.lrchan.qootalk.domain.common.BaseModel;

import lombok.Builder;

public class Message extends BaseModel {

    private Long roomId;
    private Long userId;
    private String content;
    private MessageType messageType;
    private List<Long> mentions = new ArrayList<>();
    private Long parentMessageId;

    @Builder
    protected Message(Long id, Long roomId, Long userId, String content, MessageType messageType, List<Long> mentions,
            Long parentMessageId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.roomId = Objects.requireNonNull(roomId);
        this.userId = Objects.requireNonNull(userId);
        this.content = content;
        this.messageType = (messageType == null) ? MessageType.TEXT : messageType;
        this.mentions = mentions == null ? new ArrayList<>() : new ArrayList<>(mentions);
        this.parentMessageId = parentMessageId;
    }

    public static Message create(Long roomId, Long userId, String content, MessageType messageType,
            List<Long> mentions) {
        return createReply(roomId, userId, content, messageType, mentions, null);
    }

    public static Message createReply(Long roomId, Long userId, String content, MessageType messageType,
            List<Long> mentions, Long parentMessageId) {
        return Message.builder()
                .roomId(roomId)
                .userId(userId)
                .content(content)
                .messageType(messageType)
                .mentions(mentions)
                .parentMessageId(parentMessageId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DB 복구 전용 메서드
    public static Message reconstruct(Long id, Long roomId, Long userId, String content, MessageType messageType,
            List<Long> mentions, Long parentMessageId, LocalDateTime createdAt, LocalDateTime updatedAt,
            LocalDateTime deletedAt) {
        return Message.builder()
                .id(id)
                .roomId(roomId)
                .userId(userId)
                .content(content)
                .messageType(messageType)
                .mentions(mentions)
                .parentMessageId(parentMessageId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
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
        return mentions == null ? new ArrayList<>() : new ArrayList<>(mentions);
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
        this.mentions = userIds == null ? new ArrayList<>() : new ArrayList<>(userIds);
        update();
    }
}
