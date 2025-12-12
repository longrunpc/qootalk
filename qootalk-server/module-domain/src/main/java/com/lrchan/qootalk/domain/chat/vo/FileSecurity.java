package com.lrchan.qootalk.domain.chat.vo;

import java.util.Objects;

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
        this.visibility = Objects.requireNonNull(visibility, "visibility");
        this.downloadPolicy = Objects.requireNonNull(downloadPolicy, "downloadPolicy");
        this.sharePolicy = Objects.requireNonNull(sharePolicy, "sharePolicy");
        this.scanStatus = Objects.requireNonNull(scanStatus, "scanStatus");
        this.encryption = Objects.requireNonNull(encryption, "encryption");

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
                throw new IllegalArgumentException(
                        "Malicious file cannot be downloadable or shareable"
                );
            }
        }

        if (visibility == Visibility.PUBLIC
                && downloadPolicy == DownloadPolicy.DISABLED) {
            throw new IllegalArgumentException(
                    "Public file must be downloadable"
            );
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
}
