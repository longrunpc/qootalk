package com.lrchan.qootalk.domain.chat.participant;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.common.BaseModel;

public class RoomParticipant extends BaseModel {

    private Long userId;
    private Long roomId;
    private Long lastReadMessageId;
    private RoomRole role;

    private RoomParticipant(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, LocalDateTime createdAt,
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
        this.lastReadMessageId = lastReadMessageId;
        this.role = role == null ? RoomRole.MEMBER : role;
    }

    public static RoomParticipant create(Long userId, Long roomId, Long lastReadMessageId, RoomRole role) {
        return new RoomParticipant(null, userId, roomId, lastReadMessageId, role, LocalDateTime.now(), LocalDateTime.now(), null);
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

    public void changeRole(RoomRole role) {
        this.role = role == null ? RoomRole.MEMBER : role;
        update();
    }

    public void updateReadReceipt(Long messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("Last read message ID cannot be null");
        }
        if(this.lastReadMessageId == null || messageId > this.lastReadMessageId) {
            this.lastReadMessageId = messageId;
            update();
        }
    }
}
