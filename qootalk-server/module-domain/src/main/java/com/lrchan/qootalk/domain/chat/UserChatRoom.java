package com.lrchan.qootalk.domain.chat;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class UserChatRoom extends BaseModel {

    private Long userId;
    private Long roomId;
    private RoomMemberRole role;

    private UserChatRoom(Long id, Long userId, Long roomId, RoomMemberRole role, LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.userId = userId;
        this.roomId = roomId;
        this.role = role == null ? RoomMemberRole.MEMBER : role;
    }

    public static UserChatRoom create(Long userId, Long roomId, RoomMemberRole role) {
        return new UserChatRoom(null, userId, roomId, role, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public Long userId() {
        return userId;
    }

    public Long roomId() {
        return roomId;
    }

    public RoomMemberRole role() {
        return role;
    }

    public void changeRole(RoomMemberRole role) {
        this.role = role == null ? RoomMemberRole.MEMBER : role;
        update();
    }
}
