package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Objects;

import com.lrchan.qootalk.domain.chat.vo.DownloadPolicy;
import com.lrchan.qootalk.domain.chat.vo.Encryption;
import com.lrchan.qootalk.domain.chat.vo.ScanStatus;
import com.lrchan.qootalk.domain.chat.vo.SharePolicy;
import com.lrchan.qootalk.domain.chat.vo.Visibility;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class FileSecurityEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "download_policy", nullable = false)
    private DownloadPolicy downloadPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_policy", nullable = false)
    private SharePolicy sharePolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_status", nullable = false)
    private ScanStatus scanStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "encryption", nullable = false)
    private Encryption encryption;

    protected FileSecurityEmbeddable() {
    }

    public FileSecurityEmbeddable(
            Visibility visibility,
            DownloadPolicy downloadPolicy,
            SharePolicy sharePolicy,
            ScanStatus scanStatus,
            Encryption encryption) {
        this.visibility = Objects.requireNonNull(visibility);
        this.downloadPolicy = Objects.requireNonNull(downloadPolicy);
        this.sharePolicy = Objects.requireNonNull(sharePolicy);
        this.scanStatus = Objects.requireNonNull(scanStatus);
        this.encryption = Objects.requireNonNull(encryption);
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
