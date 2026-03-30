package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.UpdateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateChatRoomQueryResult;

public interface UpdateChatRoomUsecase {

    UpdateChatRoomQueryResult update(UpdateChatRoomCommand command);
}
