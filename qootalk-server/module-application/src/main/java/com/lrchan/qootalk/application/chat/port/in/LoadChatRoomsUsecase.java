package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomsCommand;

public interface LoadChatRoomsUsecase {
    
    PagedResponse<ChatRoomQueryResult> load(LoadChatRoomsCommand command);
}
