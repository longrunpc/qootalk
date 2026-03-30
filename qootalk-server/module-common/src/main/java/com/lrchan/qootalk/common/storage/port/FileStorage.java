package com.lrchan.qootalk.common.storage.port;

import java.io.InputStream;

import com.lrchan.qootalk.common.storage.vo.StorageResource;

public interface FileStorage {
    String upload(InputStream inputStream, StorageResource resource);
    void delete(String uri);
}
