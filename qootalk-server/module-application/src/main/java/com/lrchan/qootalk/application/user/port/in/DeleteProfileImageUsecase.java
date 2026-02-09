package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.DeleateProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

public interface DeleteProfileImageUsecase {
    UserQueryResult delete(DeleateProfileImageCommand command);
}
