package com.lrchan.qootalk.domain.chat.attachment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;

@DisplayName("FileAttachment 엔티티 테스트")
class FileAttachmentTest {

    private FileMetadata createFileMetadata() {
        return new FileMetadata(
            new FileName("original.pdf"),
            new FileName("stored-uuid.pdf"),
            new ContentType("application/pdf"),
            new FileSize(1024L),
            new Path("uploads/files/"),
            StorageType.LOCAL
        );
    }

    private FileSecurity createFileSecurity() {
        return FileSecurity.defaultPrivate();
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 데이터로 FileAttachment를 생성할 수 있다")
        void should_CreateFileAttachment_When_ValidData() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            // when
            FileAttachment fileAttachment = FileAttachment.create(
                messageId,
                uploaderId,
                metadata,
                fileType,
                fileSecurity
            );

            // then
            assertThat(fileAttachment.messageId()).isEqualTo(messageId);
            assertThat(fileAttachment.uploaderId()).isEqualTo(uploaderId);
            assertThat(fileAttachment.metadata()).isEqualTo(metadata);
            assertThat(fileAttachment.fileType()).isEqualTo(fileType);
            assertThat(fileAttachment.fileSecurity()).isEqualTo(fileSecurity);
            assertThat(fileAttachment.id()).isNull();
            assertThat(fileAttachment.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("다양한 파일 타입으로 생성할 수 있다")
        void should_CreateFileAttachment_When_VariousFileTypes() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileSecurity fileSecurity = createFileSecurity();

            // when & then
            assertThat(FileAttachment.create(messageId, uploaderId, metadata, FileType.IMAGE, fileSecurity)
                .fileType()).isEqualTo(FileType.IMAGE);
            assertThat(FileAttachment.create(messageId, uploaderId, metadata, FileType.VIDEO, fileSecurity)
                .fileType()).isEqualTo(FileType.VIDEO);
            assertThat(FileAttachment.create(messageId, uploaderId, metadata, FileType.AUDIO, fileSecurity)
                .fileType()).isEqualTo(FileType.AUDIO);
            assertThat(FileAttachment.create(messageId, uploaderId, metadata, FileType.OTHER, fileSecurity)
                .fileType()).isEqualTo(FileType.OTHER);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("messageId가 null이면 예외가 발생한다")
        void should_ThrowException_When_MessageIdIsNull() {
            // given
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            // when & then
            assertThatThrownBy(() -> FileAttachment.create(
                null,
                uploaderId,
                metadata,
                fileType,
                fileSecurity
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("messageId cannot be null");
        }

        @Test
        @DisplayName("uploaderId가 null이면 예외가 발생한다")
        void should_ThrowException_When_UploaderIdIsNull() {
            // given
            Long messageId = 1L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            // when & then
            assertThatThrownBy(() -> FileAttachment.create(
                messageId,
                null,
                metadata,
                fileType,
                fileSecurity
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("uploaderId cannot be null");
        }

        @Test
        @DisplayName("metadata가 null이면 예외가 발생한다")
        void should_ThrowException_When_MetadataIsNull() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            // when & then
            assertThatThrownBy(() -> FileAttachment.create(
                messageId,
                uploaderId,
                null,
                fileType,
                fileSecurity
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("metadata");
        }

        @Test
        @DisplayName("fileType이 null이면 예외가 발생한다")
        void should_ThrowException_When_FileTypeIsNull() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileSecurity fileSecurity = createFileSecurity();

            // when & then
            assertThatThrownBy(() -> FileAttachment.create(
                messageId,
                uploaderId,
                metadata,
                null,
                fileSecurity
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fileType");
        }

        @Test
        @DisplayName("fileSecurity가 null이면 예외가 발생한다")
        void should_ThrowException_When_FileSecurityIsNull() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;

            // when & then
            assertThatThrownBy(() -> FileAttachment.create(
                messageId,
                uploaderId,
                metadata,
                fileType,
                null
            ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fileSecurity");
        }
    }
}

