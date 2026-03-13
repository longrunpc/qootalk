package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.infrastructure.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "chat_rooms")
@SQLDelete(sql = "UPDATE chat_rooms SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomEntity extends BaseEntity {

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;
}
