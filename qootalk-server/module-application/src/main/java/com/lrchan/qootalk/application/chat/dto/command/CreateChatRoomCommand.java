package com.lrchan.qootalk.application.chat.dto.command;

import java.util.List;

import com.lrchan.qootalk.domain.chat.room.RoomType;

public record CreateChatRoomCommand(
    Long requesterId,
    String roomName,
    RoomType roomType,
    List<Long> participantIds,
    boolean notificationEnabled
) {
}
