package com.lrchan.qootalk.application.user.port.in;

import com.lrchan.qootalk.application.user.dto.command.DeleateProfileImageCommand;

public interface DeleteProfileImageUsecase {
    void delete(DeleateProfileImageCommand command);
}
