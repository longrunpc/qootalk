package com.lrchan.qootalk.application.user.port.out;

import java.io.InputStream;

import com.lrchan.qootalk.common.storage.vo.StorageResource;

public interface UploadFilePort {
    String upload(InputStream inputStream, StorageResource resource);
}
