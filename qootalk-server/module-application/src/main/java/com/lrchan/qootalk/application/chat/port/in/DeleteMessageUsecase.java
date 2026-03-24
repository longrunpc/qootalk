package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.DeleteMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteMessageQueryResult;

public interface DeleteMessageUsecase {
    DeleteMessageQueryResult delete(DeleteMessageCommand command);
}
