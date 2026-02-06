package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.UploadProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;

public interface UploadprofileImageUsecase {
    UserQueryResult upload(UploadProfileImageCommand command);
}
