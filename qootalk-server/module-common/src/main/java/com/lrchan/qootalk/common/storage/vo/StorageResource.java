package com.lrchan.qootalk.common.storage.vo;

public record StorageResource(
    String path,
    String fileName,
    String contentType,
    Long fileSize
) {
}
