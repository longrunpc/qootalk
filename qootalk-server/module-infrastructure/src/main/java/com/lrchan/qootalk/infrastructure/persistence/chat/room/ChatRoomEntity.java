package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_rooms")
@SQLDelete(sql = "UPDATE chat_rooms SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ChatRoomEntity extends BaseEntity {

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    protected ChatRoomEntity() {
    }

    public ChatRoomEntity(String roomName, RoomType roomType, Long createdBy) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.createdBy = createdBy;
    }

    public ChatRoomEntity(Long id, String roomName, RoomType roomType, Long createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.roomName = roomName;
        this.roomType = roomType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public String roomName() {
        return roomName;
    }

    public RoomType roomType() {
        return roomType;
    }

    public Long createdBy() {
        return createdBy;
    }
}
