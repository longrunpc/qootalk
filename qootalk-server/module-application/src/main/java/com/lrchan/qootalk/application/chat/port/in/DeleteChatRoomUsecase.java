package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.DeleteChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteChatRoomQueryResult;

public interface DeleteChatRoomUsecase {
    DeleteChatRoomQueryResult delete(DeleteChatRoomCommand command);
}
