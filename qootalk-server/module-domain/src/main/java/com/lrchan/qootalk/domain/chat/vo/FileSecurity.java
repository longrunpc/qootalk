package com.lrchan.qootalk.domain.chat.vo;

import java.util.Objects;

import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;

public final class FileSecurity {

    private final Visibility visibility;
    private final DownloadPolicy downloadPolicy;
    private final SharePolicy sharePolicy;
    private final ScanStatus scanStatus;
    private final Encryption encryption;

    private FileSecurity(
            Visibility visibility,
            DownloadPolicy downloadPolicy,
            SharePolicy sharePolicy,
            ScanStatus scanStatus,
            Encryption encryption
    ) {
        this.visibility = visibility;
        this.downloadPolicy = downloadPolicy;
        this.sharePolicy = sharePolicy;
        this.scanStatus = scanStatus;
        this.encryption = encryption;

        validatePolicy();
    }

    public static FileSecurity defaultPrivate() {
        return new FileSecurity(
                Visibility.PRIVATE,
                DownloadPolicy.ALLOWED,
                SharePolicy.DISABLED,
                ScanStatus.PENDING,
                Encryption.NONE
        );
    }

    public static FileSecurity publicReadable() {
        return new FileSecurity(
                Visibility.PUBLIC,
                DownloadPolicy.ALLOWED,
                SharePolicy.READ_ONLY,
                ScanStatus.CLEAN,
                Encryption.NONE
        );
    }

    private void validatePolicy() {
        if (scanStatus == ScanStatus.MALICIOUS) {
            if (downloadPolicy == DownloadPolicy.ALLOWED
                    || sharePolicy != SharePolicy.DISABLED) {
                throw new DomainException(ChatErrorCode.CHAT_FILE_SECURITY_INVALID_MALICIOUS_FILE_DOWNLOADABLE_OR_SHAREABLE);
            }
        }

        if (visibility == Visibility.PUBLIC
                && downloadPolicy == DownloadPolicy.DISABLED) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_SECURITY_INVALID_PUBLIC_FILE_DOWNLOADABLE);
        }
    }

    public Visibility visibility() {
        return visibility;
    }

    public DownloadPolicy downloadPolicy() {
        return downloadPolicy;
    }

    public SharePolicy sharePolicy() {
        return sharePolicy;
    }

    public ScanStatus scanStatus() {
        return scanStatus;
    }

    public Encryption encryption() {
        return encryption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSecurity that = (FileSecurity) o;
        return visibility == that.visibility &&
                downloadPolicy == that.downloadPolicy &&
                sharePolicy == that.sharePolicy &&
                scanStatus == that.scanStatus &&
                encryption == that.encryption;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, downloadPolicy, sharePolicy, scanStatus, encryption);
    }
}
