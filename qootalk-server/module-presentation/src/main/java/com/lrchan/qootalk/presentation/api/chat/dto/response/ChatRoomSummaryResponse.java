package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;

import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.domain.chat.room.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 목록 항목 응답")
public record ChatRoomSummaryResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long id,
    @Schema(description = "채팅방 이름", example = "백엔드 팀")
    String roomName,
    @Schema(description = "채팅방 타입", example = "GROUP")
    RoomType roomType,
    @Schema(description = "마지막 메시지", example = "오늘 배포 일정 공유드립니다.")
    String lastMessage,
    @Schema(description = "읽지 않은 메시지 수", example = "3")
    int unreadCount,
    @Schema(description = "마지막 갱신 일시", example = "2026-03-12T11:05:00")
    LocalDateTime updatedAt
) {
    public static ChatRoomSummaryResponse of(ChatRoomQueryResult result) {
        return new ChatRoomSummaryResponse(
            result.id(),
            result.roomName(),
            result.roomType(),
            result.lastMessage(),
            result.unreadCount(),
            result.updatedAt()
        );
    }
}
