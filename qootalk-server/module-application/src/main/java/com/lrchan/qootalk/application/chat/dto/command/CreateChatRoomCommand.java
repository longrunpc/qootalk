package com.lrchan.qootalk.application.chat.dto.command;

import java.util.List;

import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.domain.chat.vo.RoomName;

public record CreateChatRoomCommand(
    RoomName roomName,
    RoomType roomType,
    List<Long> participantIds,
    boolean notificationEnabled
) {
}
