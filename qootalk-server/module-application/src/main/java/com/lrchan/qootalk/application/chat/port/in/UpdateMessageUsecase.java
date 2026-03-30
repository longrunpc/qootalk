package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.UpdateMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateMessageQueryResult;

public interface UpdateMessageUsecase {
    UpdateMessageQueryResult update(UpdateMessageCommand command);
}
