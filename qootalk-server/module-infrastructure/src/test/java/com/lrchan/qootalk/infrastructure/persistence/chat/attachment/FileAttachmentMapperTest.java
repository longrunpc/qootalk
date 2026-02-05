package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.DownloadPolicy;
import com.lrchan.qootalk.domain.chat.vo.Encryption;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.ScanStatus;
import com.lrchan.qootalk.domain.chat.vo.SharePolicy;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.domain.chat.vo.Visibility;

@DisplayName("FileAttachmentMapper 테스트")
class FileAttachmentMapperTest {

    @Nested
    @DisplayName("도메인으로 변환")
    class ToDomainTest {

        @Test
        @DisplayName("FileAttachmentEntity를 FileAttachment 도메인으로 변환할 수 있다")
        void should_ConvertToDomain_When_ValidEntity() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deletedAt = LocalDateTime.now();

            FileMetadataEmbeddable metadataEmbeddable = new FileMetadataEmbeddable(
                    "photo.jpg",
                    "abc123.jpg",
                    "image/jpeg",
                    1024000L,
                    "uploads/2024/01/",
                    StorageType.LOCAL
            );

            FileSecurityEmbeddable securityEmbeddable = new FileSecurityEmbeddable(
                    Visibility.PRIVATE,
                    DownloadPolicy.ALLOWED,
                    SharePolicy.DISABLED,
                    ScanStatus.CLEAN,
                    Encryption.NONE
            );

            FileAttachmentEntity entity = FileAttachmentEntity.builder()
                    .id(1L)
                    .messageId(100L)
                    .uploaderId(50L)
                    .metadata(metadataEmbeddable)
                    .fileType(FileType.IMAGE)
                    .security(securityEmbeddable)
                    .createdAt(now)
                    .updatedAt(now)
                    .deletedAt(deletedAt)
                    .build();

            // when
            FileAttachment domain = FileAttachmentMapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.messageId()).isEqualTo(100L);
            assertThat(domain.uploaderId()).isEqualTo(50L);
            assertThat(domain.fileType()).isEqualTo(FileType.IMAGE);
            assertThat(domain.createdAt()).isEqualTo(now);
            assertThat(domain.updatedAt()).isEqualTo(now);
            assertThat(domain.deletedAt()).isEqualTo(deletedAt);
            assertThat(domain.isDeleted()).isTrue();

            // FileMetadata 검증
            assertThat(domain.metadata().originalFileName().value()).isEqualTo("photo.jpg");
            assertThat(domain.metadata().storedFileName().value()).isEqualTo("abc123.jpg");
            assertThat(domain.metadata().contentType().value()).isEqualTo("image/jpeg");
            assertThat(domain.metadata().fileSize().value()).isEqualTo(1024000L);
            assertThat(domain.metadata().storagePath().value()).isEqualTo("uploads/2024/01/");
            assertThat(domain.metadata().storageType()).isEqualTo(StorageType.LOCAL);

            // FileSecurity 검증
            assertThat(domain.fileSecurity().visibility()).isEqualTo(Visibility.PRIVATE);
            assertThat(domain.fileSecurity().downloadPolicy()).isEqualTo(DownloadPolicy.ALLOWED);
            assertThat(domain.fileSecurity().sharePolicy()).isEqualTo(SharePolicy.DISABLED);
            assertThat(domain.fileSecurity().scanStatus()).isEqualTo(ScanStatus.CLEAN);
            assertThat(domain.fileSecurity().encryption()).isEqualTo(Encryption.NONE);
        }

        @Test
        @DisplayName("fileType이 null인 FileAttachmentEntity를 FileAttachment 도메인으로 변환하면 기본값이 적용된다")
        void should_ConvertToDomain_When_FileTypeIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileMetadataEmbeddable metadataEmbeddable = new FileMetadataEmbeddable(
                    "document.pdf",
                    "def456.pdf",
                    "application/pdf",
                    2048000L,
                    "uploads/2024/01/",
                    StorageType.LOCAL
            );

            FileSecurityEmbeddable securityEmbeddable = new FileSecurityEmbeddable(
                    Visibility.PUBLIC,
                    DownloadPolicy.ALLOWED,
                    SharePolicy.READ_ONLY,
                    ScanStatus.CLEAN,
                    Encryption.NONE
            );

            FileAttachmentEntity entity = FileAttachmentEntity.builder()
                    .id(1L)
                    .messageId(100L)
                    .uploaderId(50L)
                    .metadata(metadataEmbeddable)
                    .fileType(null)
                    .security(securityEmbeddable)
                    .createdAt(now)
                    .updatedAt(now)
                    .deletedAt(null)
                    .build();

            // when
            FileAttachment domain = FileAttachmentMapper.toDomain(entity);

            // then
            assertThat(domain.fileType()).isEqualTo(FileType.DOCUMENT);
            assertThat(domain.deletedAt()).isNull();
            assertThat(domain.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("엔티티로 변환")
    class ToEntityTest {

        @Test
        @DisplayName("FileAttachment 도메인을 FileAttachmentEntity로 변환할 수 있다")
        void should_ConvertToEntity_When_ValidDomain() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileMetadata metadata = new FileMetadata(
                    new FileName("video.mp4"),
                    new FileName("ghi789.mp4"),
                    new ContentType("video/mp4"),
                    new FileSize(5120000L),
                    new Path("uploads/2024/01/"),
                    StorageType.LOCAL
            );

            FileSecurity security = FileSecurity.reconstruct(
                    Visibility.PUBLIC,
                    DownloadPolicy.ALLOWED,
                    SharePolicy.READ_ONLY,
                    ScanStatus.CLEAN,
                    Encryption.NONE
            );

            FileAttachment domain = FileAttachment.reconstruct(
                    1L,
                    100L,
                    50L,
                    metadata,
                    FileType.VIDEO,
                    security,
                    now,
                    now,
                    null
            );

            // when
            FileAttachmentEntity entity = FileAttachmentMapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getMessageId()).isEqualTo(100L);
            assertThat(entity.getUploaderId()).isEqualTo(50L);
            assertThat(entity.getFileType()).isEqualTo(FileType.VIDEO);
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
            assertThat(entity.getDeletedAt()).isNull();

            // FileMetadataEmbeddable 검증
            assertThat(entity.getMetadata().originalFileName()).isEqualTo("video.mp4");
            assertThat(entity.getMetadata().storedFileName()).isEqualTo("ghi789.mp4");
            assertThat(entity.getMetadata().contentType()).isEqualTo("video/mp4");
            assertThat(entity.getMetadata().fileSize()).isEqualTo(5120000L);
            assertThat(entity.getMetadata().storagePath()).isEqualTo("uploads/2024/01/");
            assertThat(entity.getMetadata().storageType()).isEqualTo(StorageType.LOCAL);

            // FileSecurityEmbeddable 검증
            assertThat(entity.getSecurity().visibility()).isEqualTo(Visibility.PUBLIC);
            assertThat(entity.getSecurity().downloadPolicy()).isEqualTo(DownloadPolicy.ALLOWED);
            assertThat(entity.getSecurity().sharePolicy()).isEqualTo(SharePolicy.READ_ONLY);
            assertThat(entity.getSecurity().scanStatus()).isEqualTo(ScanStatus.CLEAN);
            assertThat(entity.getSecurity().encryption()).isEqualTo(Encryption.NONE);
        }

        @Test
        @DisplayName("fileType이 null인 FileAttachment 도메인을 FileAttachmentEntity로 변환하면 기본값이 적용된다")
        void should_ConvertToEntity_When_FileTypeIsNull() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileMetadata metadata = new FileMetadata(
                    new FileName("audio.mp3"),
                    new FileName("jkl012.mp3"),
                    new ContentType("audio/mpeg"),
                    new FileSize(3072000L),
                    new Path("uploads/2024/01/"),
                    StorageType.LOCAL
            );

            FileSecurity security = FileSecurity.defaultPrivate();

            FileAttachment domain = FileAttachment.reconstruct(
                    1L,
                    100L,
                    50L,
                    metadata,
                    null,
                    security,
                    now,
                    now,
                    null
            );

            // when
            FileAttachmentEntity entity = FileAttachmentMapper.toEntity(domain);

            // then
            assertThat(entity.getFileType()).isEqualTo(FileType.DOCUMENT);
        }
    }

    @Nested
    @DisplayName("양방향 변환")
    class RoundTripTest {

        @Test
        @DisplayName("FileAttachment 도메인을 FileAttachmentEntity로 변환하고 다시 FileAttachment 도메인으로 변환하면 동일한 값이 유지된다")
        void should_MaintainValues_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();

            FileMetadata originalMetadata = new FileMetadata(
                    new FileName("document.pdf"),
                    new FileName("mno345.pdf"),
                    new ContentType("application/pdf"),
                    new FileSize(4096000L),
                    new Path("uploads/2024/01/"),
                    StorageType.LOCAL
            );

            FileSecurity originalSecurity = FileSecurity.reconstruct(
                    Visibility.PRIVATE,
                    DownloadPolicy.ALLOWED,
                    SharePolicy.DISABLED,
                    ScanStatus.PENDING,
                    Encryption.NONE
            );

            FileAttachment original = FileAttachment.reconstruct(
                    1L,
                    100L,
                    50L,
                    originalMetadata,
                    FileType.DOCUMENT,
                    originalSecurity,
                    now,
                    now,
                    null
            );

            // when
            FileAttachmentEntity entity = FileAttachmentMapper.toEntity(original);
            FileAttachment converted = FileAttachmentMapper.toDomain(entity);

            // then
            assertThat(converted.id()).isEqualTo(original.id());
            assertThat(converted.messageId()).isEqualTo(original.messageId());
            assertThat(converted.uploaderId()).isEqualTo(original.uploaderId());
            assertThat(converted.fileType()).isEqualTo(original.fileType());
            assertThat(converted.createdAt()).isEqualTo(original.createdAt());
            assertThat(converted.updatedAt()).isEqualTo(original.updatedAt());
            assertThat(converted.deletedAt()).isEqualTo(original.deletedAt());

            // FileMetadata 검증
            assertThat(converted.metadata().originalFileName().value())
                    .isEqualTo(original.metadata().originalFileName().value());
            assertThat(converted.metadata().storedFileName().value())
                    .isEqualTo(original.metadata().storedFileName().value());
            assertThat(converted.metadata().contentType().value())
                    .isEqualTo(original.metadata().contentType().value());
            assertThat(converted.metadata().fileSize().value())
                    .isEqualTo(original.metadata().fileSize().value());
            assertThat(converted.metadata().storagePath().value())
                    .isEqualTo(original.metadata().storagePath().value());
            assertThat(converted.metadata().storageType())
                    .isEqualTo(original.metadata().storageType());

            // FileSecurity 검증
            assertThat(converted.fileSecurity().visibility())
                    .isEqualTo(original.fileSecurity().visibility());
            assertThat(converted.fileSecurity().downloadPolicy())
                    .isEqualTo(original.fileSecurity().downloadPolicy());
            assertThat(converted.fileSecurity().sharePolicy())
                    .isEqualTo(original.fileSecurity().sharePolicy());
            assertThat(converted.fileSecurity().scanStatus())
                    .isEqualTo(original.fileSecurity().scanStatus());
            assertThat(converted.fileSecurity().encryption())
                    .isEqualTo(original.fileSecurity().encryption());
        }

        @Test
        @DisplayName("deletedAt이 설정된 FileAttachment 도메인을 양방향 변환해도 올바르게 처리된다")
        void should_HandleDeletedAt_When_RoundTrip() {
            // given
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deletedAt = LocalDateTime.now();

            FileMetadata metadata = new FileMetadata(
                    new FileName("file.txt"),
                    new FileName("pqr678.txt"),
                    new ContentType("text/plain"),
                    new FileSize(1024L),
                    new Path("uploads/2024/01/"),
                    StorageType.LOCAL
            );

            FileSecurity security = FileSecurity.reconstruct(
                    Visibility.PRIVATE,
                    DownloadPolicy.DISABLED,
                    SharePolicy.DISABLED,
                    ScanStatus.MALICIOUS,
                    Encryption.NONE
            );

            FileAttachment original = FileAttachment.reconstruct(
                    1L,
                    100L,
                    50L,
                    metadata,
                    FileType.OTHER,
                    security,
                    now,
                    now,
                    deletedAt
            );

            // when
            FileAttachmentEntity entity = FileAttachmentMapper.toEntity(original);
            FileAttachment converted = FileAttachmentMapper.toDomain(entity);

            // then
            assertThat(converted.deletedAt()).isEqualTo(deletedAt);
            assertThat(converted.isDeleted()).isTrue();
            assertThat(converted.fileSecurity().scanStatus()).isEqualTo(ScanStatus.MALICIOUS);
        }
    }
}
