package com.lrchan.qootalk.application.chat.dto.result;

import java.time.LocalDateTime;

import com.lrchan.qootalk.domain.chat.participant.RoomRole;

public record ParticipantResult(
    Long userId,
    RoomRole roomRole,
    LocalDateTime joinedAt
) {
}
