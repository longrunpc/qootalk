package com.lrchan.qootalk.domain.chat.vo;

public record FileUploadCommand(FileName originalFileName, FileSize fileSize, ContentType contentType) {
}
