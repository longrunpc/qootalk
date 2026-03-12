package com.lrchan.qootalk.application.chat.port.in;

import java.util.List;

import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.dto.command.ReadChatRoomsCommand;

public interface ReadChatRoomsUsecase {
    
    List<ChatRoomQueryResult> read(ReadChatRoomsCommand command);
}
