package com.lrchan.qootalk.domain.chat.attachment;

import java.io.InputStream;

import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileUploadCommand;

public interface FileStorage {
    FileMetadata upload(InputStream inputStream, FileUploadCommand command);
    void delete(FileMetadata metadata);
}
