package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.UpdateStatusMessageCommand;

public interface UpdateStatusMessageUsecase {
    void update(UpdateStatusMessageCommand command);
}
