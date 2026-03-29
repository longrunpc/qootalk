package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatHistoriesCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatHistoryQueryResult;
import com.lrchan.qootalk.common.response.SliceResponse;

public interface LoadChatHistoriesUsecase {
    SliceResponse<ChatHistoryQueryResult> load(LoadChatHistoriesCommand command);
}
