package com.lrchan.qootalk.domain.chat.room;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.vo.RoomName;
import com.lrchan.qootalk.domain.common.BaseModel;

public class ChatRoom extends BaseModel {

    private RoomName roomName;
    private RoomType roomType;
    private Long createdBy;

    private ChatRoom(Long id, RoomName roomName, RoomType roomType, Long createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.roomName = roomName;
        this.roomType = roomType;
        this.createdBy = createdBy;
    }

    public static ChatRoom create(String roomName, RoomType roomType, Long createdBy) {
        return new ChatRoom(null, new RoomName(roomName), roomType, createdBy, LocalDateTime.now(), LocalDateTime.now(), null);
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
