package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.UpdateChatRoomQueryResult;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 수정 응답")
public record UpdateChatRoomResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long id,
    @Schema(description = "수정된 채팅방 이름", example = "백엔드 플랫폼 팀")
    String roomName,
    @Schema(description = "수정 일시", example = "2026-03-12T11:10:00")
    LocalDateTime updatedAt
) {
    public static UpdateChatRoomResponse of(UpdateChatRoomQueryResult result) {
        return new UpdateChatRoomResponse(result.id(), result.roomName(), result.updatedAt());
    }
}
