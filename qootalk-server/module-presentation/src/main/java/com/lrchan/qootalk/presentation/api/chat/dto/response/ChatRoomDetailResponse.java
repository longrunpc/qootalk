package com.lrchan.qootalk.presentation.api.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;
import com.lrchan.qootalk.domain.chat.room.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 상세 응답")
public record ChatRoomDetailResponse(
    @Schema(description = "채팅방 ID", example = "10")
    Long id,
    @Schema(description = "채팅방 이름", example = "백엔드 팀")
    String roomName,
    @Schema(description = "채팅방 타입", example = "GROUP")
    RoomType roomType,
    @Schema(description = "생성자 사용자 ID", example = "1")
    Long createdBy,
    @Schema(description = "참여자 목록")
    List<ChatRoomParticipantResponse> participants,
    @Schema(description = "알림 활성화 여부", example = "true")
    boolean notificationEnabled,
    @Schema(description = "생성 일시", example = "2026-03-12T11:00:00")
    LocalDateTime createdAt
) {
    public static ChatRoomDetailResponse of(ChatRoomDetailQueryResult result) {
        return new ChatRoomDetailResponse(
            result.id(),
            result.roomName(),
            result.roomType(),
            result.createdBy(),
            result.participants().stream().map(ChatRoomParticipantResponse::of).toList(),
            result.notificationEnabled(),
            result.createdAt()
        );
    }
}
