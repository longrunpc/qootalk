package com.lrchan.qootalk.domain.chat.participant;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.common.BaseModel;

import lombok.Builder;

public class RoomParticipant extends BaseModel {

    private Long userId;
    private Long roomId;
    private Long lastReadMessageId;
    private RoomRole role;

    @Builder
    protected RoomParticipant(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime deletedAt) {
        super(id, createdAt, updatedAt, deletedAt);
        this.userId = Objects.requireNonNull(userId);
        this.roomId = Objects.requireNonNull(roomId);
        this.lastReadMessageId = Objects.requireNonNull(lastReadMessageId);
        this.role = role == null ? RoomRole.MEMBER : role;
    }

    public static RoomParticipant create(Long userId, Long roomId, Long lastReadMessageId, RoomRole role) {
        return RoomParticipant.builder()
                .userId(userId)
                .roomId(roomId)
                .lastReadMessageId(lastReadMessageId)
                .role(role)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // DB 복구 전용 메서드
    public static RoomParticipant reconstruct(Long id, Long userId, Long roomId, Long lastReadMessageId, RoomRole role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return RoomParticipant.builder()
                .id(id)
                .userId(userId)
                .roomId(roomId)
                .lastReadMessageId(lastReadMessageId)
                .role(role)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
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
        if (messageId == null || messageId <= this.lastReadMessageId) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID);
        }
        this.lastReadMessageId = messageId;
        update();
    }
}
