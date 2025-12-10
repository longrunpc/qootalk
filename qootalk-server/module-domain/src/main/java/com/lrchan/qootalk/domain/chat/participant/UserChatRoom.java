package com.lrchan.qootalk.domain.chat;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class UserChatRoom extends BaseModel {

    private Long userId;
    private Long roomId;
    private RoomRole role;

    private UserChatRoom(Long id, Long userId, Long roomId, RoomRole role, LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("Room ID cannot be null");
        }
        this.userId = userId;
        this.roomId = roomId;
        this.role = role == null ? RoomRole.MEMBER : role;
    }

    public static UserChatRoom create(Long userId, Long roomId, RoomRole role) {
        return new UserChatRoom(null, userId, roomId, role, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Long userId() {
        return userId;
    }

    public Long roomId() {
        return roomId;
    }

    public RoomRole role() {
        return role;
    }

    public void changeRole(RoomRole role) {
        this.role = role == null ? RoomRole.MEMBER : role;
        update();
    }
}
