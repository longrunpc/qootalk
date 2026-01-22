package com.lrchan.qootalk.domain.chat.room;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.chat.vo.RoomName;
import com.lrchan.qootalk.domain.common.BaseModel;

import lombok.Builder;

public class ChatRoom extends BaseModel {

    private RoomName roomName;
    private RoomType roomType;
    private Long createdBy;

    @Builder
    protected ChatRoom(Long id, RoomName roomName, RoomType roomType, Long createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.roomName = Objects.requireNonNull(roomName);
        this.roomType = roomType == null ? RoomType.DIRECT : roomType;
        this.createdBy = Objects.requireNonNull(createdBy);
    }

    public static ChatRoom create(String roomName, RoomType roomType, Long createdBy) {
        return ChatRoom.builder()
                .roomName(new RoomName(roomName))
                .roomType(roomType)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DB 복구 전용 메서드
    public static ChatRoom reconstruct(Long id, RoomName roomName, RoomType roomType, Long createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return ChatRoom.builder()
                .id(id)
                .roomName(roomName)
                .roomType(roomType)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    public String roomName() {
        return roomName.value();
    }

    public RoomType roomType() {
        return roomType;
    }

    public Long createdBy() {
        return createdBy;
    }

    public void changeRoomName(String roomName) {
        this.roomName = new RoomName(roomName);
        update();
    }

    public void changeCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
        update();
    }
}
