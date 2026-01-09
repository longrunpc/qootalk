package com.lrchan.qootalk.infrastructure.persistence.chat.participant;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_participants")
@SQLDelete(sql = "UPDATE room_participants SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class RoomParticipantEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoomRole role;

    protected RoomParticipantEntity() {
    }

    public RoomParticipantEntity(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId);
        this.roomId = Objects.requireNonNull(roomId);
        this.lastReadMessageId = Objects.requireNonNull(lastReadMessageId);
        this.role = role == null ? RoomRole.MEMBER : role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    
    public RoomParticipantEntity(Long userId, Long roomId, Long lastReadMessageId, RoomRole role) {
        this.userId = Objects.requireNonNull(userId);
        this.roomId = Objects.requireNonNull(roomId);
        this.lastReadMessageId = Objects.requireNonNull(lastReadMessageId);
        this.role = role == null ? RoomRole.MEMBER : role;
    }

    public Long userId() {
        return userId;
    }

    public Long roomId() {
        return roomId;
    }
    
    public Long lastReadMessageId() {
        return lastReadMessageId;
    }

    public RoomRole role() {
        return role;
    }
}
