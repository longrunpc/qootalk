package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
@SQLDelete(sql = "UPDATE messages SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class MessageEntity extends BaseEntity {
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 파일 메시지일 때 내용이 없을 수도 있음
    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    // null일때 멘션 없음을 의미(체크 유의)
    @Column(name = "mentions")
    private List<Long> mentions;

    @Column(name = "parent_message_id")
    private Long parentMessageId;

    protected MessageEntity() {
    }

    public MessageEntity(Long id, Long roomId, Long userId, String content, MessageType messageType, List<Long> mentions, Long parentMessageId, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.roomId = Objects.requireNonNull(roomId);
        this.userId = Objects.requireNonNull(userId);
        this.content = content;
        this.messageType = (messageType == null) ? MessageType.TEXT : messageType;
        this.mentions = mentions == null ? null : new ArrayList<>(mentions);
        this.parentMessageId = parentMessageId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    
    public MessageEntity(Long roomId, Long userId, String content, MessageType messageType, List<Long> mentions, Long parentMessageId) {
        this.roomId = Objects.requireNonNull(roomId);
        this.userId = Objects.requireNonNull(userId);
        this.content = content;
        this.messageType = (messageType == null) ? MessageType.TEXT : messageType;
        this.mentions = mentions == null ? null : new ArrayList<>(mentions);
        this.parentMessageId = parentMessageId;
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
        return mentions == null ? null : new ArrayList<>(mentions);
    }

    public Long parentMessageId() {
        return parentMessageId;
    }
}
