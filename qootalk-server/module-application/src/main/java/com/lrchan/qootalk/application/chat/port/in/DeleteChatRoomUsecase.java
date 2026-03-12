package com.lrchan.qootalk.application.chat.port.in;

public interface DeleteChatRoomUsecase {
    DeleteChatRoomQueryResult delete(DeleteChatRoomCommand command);
}
