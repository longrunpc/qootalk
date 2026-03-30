package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.SendMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.SendMessageQueryResult;

public interface SendMessageUsecase {
    SendMessageQueryResult send(SendMessageCommand command);
}
