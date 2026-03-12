package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;

public interface LoadChatRoomDetailUsecase {
    ChatRoomDetailQueryResult read(Long roomId);
}
