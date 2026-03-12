package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.CreateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;

public interface CreateChatRoomUsecase {
    CreateChatRoomQueryResult create(CreateChatRoomCommand command);
}
