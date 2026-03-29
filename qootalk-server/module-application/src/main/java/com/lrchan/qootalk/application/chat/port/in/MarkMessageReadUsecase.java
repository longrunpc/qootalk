package com.lrchan.qootalk.application.chat.port.in;

import com.lrchan.qootalk.application.chat.dto.command.MarkMessageReadCommand;
import com.lrchan.qootalk.application.chat.dto.result.ReadReceiptQueryResult;

public interface MarkMessageReadUsecase {
    ReadReceiptQueryResult mark(MarkMessageReadCommand command);
}
