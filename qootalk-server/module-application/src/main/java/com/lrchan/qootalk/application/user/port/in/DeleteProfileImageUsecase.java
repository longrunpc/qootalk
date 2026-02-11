package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.DeleteProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

public interface DeleteProfileImageUsecase {
    UserQueryResult delete(DeleteProfileImageCommand command);
}
