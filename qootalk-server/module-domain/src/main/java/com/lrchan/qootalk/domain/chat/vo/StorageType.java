package com.lrchan.qootalk.domain.chat.vo;

public enum StorageType {
    LOCAL,
    S3,
    CDN,
    TEMP,
    EXTERNAL;

    public boolean isLocal() {
        return this == LOCAL;
    }

    public boolean isRemote() {
        return this == S3 || this == CDN || this == EXTERNAL;
    }

    public boolean isTemp() {
        return this == TEMP;
    }
}
