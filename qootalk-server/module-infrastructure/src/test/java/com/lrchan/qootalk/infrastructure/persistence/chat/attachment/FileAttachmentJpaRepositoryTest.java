package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.vo.DownloadPolicy;
import com.lrchan.qootalk.domain.chat.vo.Encryption;
import com.lrchan.qootalk.domain.chat.vo.ScanStatus;
import com.lrchan.qootalk.domain.chat.vo.SharePolicy;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.domain.chat.vo.Visibility;
import com.lrchan.qootalk.infrastructure.PostgresDBIntegrationTestSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class FileAttachmentJpaRepositoryTest extends PostgresDBIntegrationTestSupport {

    @Autowired
    private FileAttachmentJpaRepository fileAttachmentJpaRepository;

    @Test
    @DisplayName("FileAttachmentEntity 저장 및 조회 테스트")
    void should_saveAndFind_when_validAttachment() {
        // given
        FileAttachmentEntity fileAttachmentEntity = FileAttachmentEntity.builder()
            .messageId(1L)
            .uploaderId(2L)
            .metadata(new FileMetadataEmbeddable(
                "origin.png",
                "stored.png",
                "image/png",
                1234L,
                "/tmp",
                StorageType.LOCAL
            ))
            .fileType(FileType.IMAGE)
            .security(new FileSecurityEmbeddable(
                Visibility.PRIVATE,
                DownloadPolicy.ALLOWED,
                SharePolicy.DISABLED,
                ScanStatus.CLEAN,
                Encryption.NONE
            ))
            .build();

        // when
        FileAttachmentEntity savedEntity = fileAttachmentJpaRepository.save(fileAttachmentEntity);

        // then
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getMessageId()).isEqualTo(1L);
        assertThat(savedEntity.getUploaderId()).isEqualTo(2L);
        assertThat(savedEntity.getFileType()).isEqualTo(FileType.IMAGE);
        assertThat(savedEntity.getMetadata().originalFileName()).isEqualTo("origin.png");
        assertThat(savedEntity.getSecurity().visibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();
    }

    @Nested
    @DisplayName("메시지 ID 조회 테스트")
    class FindByMessageIdTest {
        @Test
        @DisplayName("메시지 ID 존재 시")
        void should_findByMessageId_when_validMessageId() {
            // given
            FileAttachmentEntity fileAttachmentEntity = FileAttachmentEntity.builder()
                .messageId(1L)
                .uploaderId(2L)
                .metadata(new FileMetadataEmbeddable(
                    "origin.png",
                    "stored.png",
                    "image/png",
                    1234L,
                    "/tmp",
                    StorageType.LOCAL
                ))
                .fileType(FileType.IMAGE)
                .security(new FileSecurityEmbeddable(
                    Visibility.PRIVATE,
                    DownloadPolicy.ALLOWED,
                    SharePolicy.DISABLED,
                    ScanStatus.CLEAN,
                    Encryption.NONE
                ))
                .build();
            fileAttachmentJpaRepository.save(fileAttachmentEntity);

            // when
            FileAttachmentEntity foundEntity = fileAttachmentJpaRepository.findByMessageId(1L).orElseThrow();

            // then
            assertThat(foundEntity.getId()).isNotNull();
            assertThat(foundEntity.getMessageId()).isEqualTo(1L);
            assertThat(foundEntity.getUploaderId()).isEqualTo(2L);
            assertThat(foundEntity.getFileType()).isEqualTo(FileType.IMAGE);
        }

        @Test
        @DisplayName("메시지 ID 조회 실패 시 빈 값 반환")
        void should_returnEmpty_when_invalidMessageId() {
            // when & then
            assertThat(fileAttachmentJpaRepository.findByMessageId(999L)).isEmpty();
        }
    }
}
