package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.UpdateStatusMessageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

public interface UpdateStatusMessageUsecase {
    UserQueryResult update(UpdateStatusMessageCommand command);
}
