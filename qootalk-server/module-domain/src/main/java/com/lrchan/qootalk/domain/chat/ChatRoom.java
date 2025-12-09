package com.lrchan.qootalk.domain.chat;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class ChatRoom extends BaseModel {

    private RoomName roomName;
    private RoomType roomType;
    private Long createBy;

    private ChatRoom(Long id, RoomName roomName, RoomType roomType, Long createBy, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.roomName = roomName;
        this.roomType = roomType;
        this.createBy = createBy;
    }

    public static ChatRoom create(String roomName, RoomType roomType, Long createBy) {
        return new ChatRoom(null, new RoomName(roomName), roomType, createBy, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public String roomName() {
        return roomName.value();
    }

    public RoomType roomType() {
        return roomType;
    }

    public Long createBy() {
        return createBy;
    }

    public void changeRoomName(String roomName) {
        this.roomName = new RoomName(roomName);
        update();
    }

    public void changeCreateBy(Long createBy) {
        this.createBy = createBy;
        update();
    }

    public void softDelete() {
        super.softDelete();
    }
}
