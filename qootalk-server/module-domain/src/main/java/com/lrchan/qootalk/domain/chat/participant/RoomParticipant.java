package com.lrchan.qootalk.domain.chat.participant;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.common.BaseModel;

public class RoomParticipant extends BaseModel {

    private Long userId;
    private Long roomId;
    private Long lastReadMessageId;
    private RoomRole role;
    private boolean notificationEnabled;

    private RoomParticipant(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role,
            boolean notificationEnabled, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.userId = Objects.requireNonNull(userId);
        this.roomId = Objects.requireNonNull(roomId);
        this.lastReadMessageId = Objects.requireNonNull(lastReadMessageId);
        this.role = role == null ? RoomRole.MEMBER : role;
        this.notificationEnabled = notificationEnabled;
    }

    public static RoomParticipant create(Long userId, Long roomId, Long lastReadMessageId, RoomRole role) {
        return create(userId, roomId, lastReadMessageId, role, true);
    }

    public static RoomParticipant create(Long userId, Long roomId, Long lastReadMessageId, RoomRole role, boolean notificationEnabled) {
        return new RoomParticipant(null, userId, roomId, lastReadMessageId, role, notificationEnabled, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    // DB 복구 전용 메서드
    public static RoomParticipant reconstruct(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return reconstruct(id, userId, roomId, lastReadMessageId, role, true, createdAt, updatedAt, deletedAt);
    }

    public static RoomParticipant reconstruct(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role,
            boolean notificationEnabled, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new RoomParticipant(id, userId, roomId, lastReadMessageId, role, notificationEnabled, createdAt, updatedAt, deletedAt);
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

    public boolean notificationEnabled() {
        return notificationEnabled;
    }

    public void changeRole(RoomRole role) {
        this.role = role == null ? RoomRole.MEMBER : role;
        update();
    }

    public void changeNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        update();
    }

    public void updateReadReceipt(Long messageId) {
        if (messageId == null || messageId <= this.lastReadMessageId) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID);
        }
        this.lastReadMessageId = messageId;
        update();
    }
}
