package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.ParticipantResult;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 참여자 응답")
public record ChatRoomParticipantResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long userId,
    @Schema(description = "방 역할", example = "OWNER")
    RoomRole roomRole,
    @Schema(description = "참여 일시", example = "2026-03-12T11:00:00")
    LocalDateTime joinedAt
) {
    public static ChatRoomParticipantResponse of(ParticipantResult result) {
        return new ChatRoomParticipantResponse(result.userId(), result.roomRole(), result.joinedAt());
    }
}
