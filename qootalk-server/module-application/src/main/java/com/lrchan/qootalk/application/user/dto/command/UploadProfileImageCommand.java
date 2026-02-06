package com.lrchan.qootalk.application.user.dto.command;

import java.io.InputStream;

public record UploadProfileImageCommand(
    Long userId,
    InputStream inputStream,
    String originalFileName,
    String contentType,
    long fileSize
) {
}
