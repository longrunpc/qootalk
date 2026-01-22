package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.persistence.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chat_rooms")
@SQLDelete(sql = "UPDATE chat_rooms SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
