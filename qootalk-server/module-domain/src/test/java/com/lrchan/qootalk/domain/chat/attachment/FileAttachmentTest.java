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

        @Test
        @DisplayName("다양한 FileSecurity 설정으로 생성할 수 있다")
        void should_CreateFileAttachment_When_VariousFileSecurity() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;

            // when & then
            FileAttachment privateAttachment = FileAttachment.create(
                messageId, uploaderId, metadata, fileType, FileSecurity.defaultPrivate()
            );
            assertThat(privateAttachment.fileSecurity().visibility()).isEqualTo(com.lrchan.qootalk.domain.chat.vo.Visibility.PRIVATE);

            FileAttachment publicAttachment = FileAttachment.create(
                messageId, uploaderId, metadata, fileType, FileSecurity.publicReadable()
            );
            assertThat(publicAttachment.fileSecurity().visibility()).isEqualTo(com.lrchan.qootalk.domain.chat.vo.Visibility.PUBLIC);
        }

        @Test
        @DisplayName("다양한 FileMetadata로 생성할 수 있다")
        void should_CreateFileAttachment_When_VariousFileMetadata() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileType fileType = FileType.IMAGE;
            FileSecurity fileSecurity = createFileSecurity();

            // when
            FileMetadata imageMetadata = new FileMetadata(
                new FileName("image.jpg"),
                new FileName("stored-image.jpg"),
                new ContentType("image/jpeg"),
                new FileSize(2048L),
                new Path("uploads/images/"),
                StorageType.LOCAL
            );

            FileAttachment fileAttachment = FileAttachment.create(
                messageId,
                uploaderId,
                imageMetadata,
                fileType,
                fileSecurity
            );

            // then
            assertThat(fileAttachment.metadata()).isEqualTo(imageMetadata);
            assertThat(fileAttachment.metadata().contentType().value()).isEqualTo("image/jpeg");
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

    @Nested
    @DisplayName("BaseModel 기능")
    class BaseModelTest {

        @Test
        @DisplayName("생성 시 id는 null이다")
        void should_HaveNullId_When_Created() {
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
            assertThat(fileAttachment.id()).isNull();
        }

        @Test
        @DisplayName("생성 시 삭제되지 않은 상태이다")
        void should_NotBeDeleted_When_Created() {
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
            assertThat(fileAttachment.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("softDelete를 호출하면 삭제 상태가 된다")
        void should_BeDeleted_When_SoftDeleteCalled() {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            FileAttachment fileAttachment = FileAttachment.create(
                messageId,
                uploaderId,
                metadata,
                fileType,
                fileSecurity
            );

            // when
            fileAttachment.softDelete();

            // then
            assertThat(fileAttachment.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("update를 호출하면 updatedAt이 갱신된다")
        void should_UpdateTimestamp_When_UpdateCalled() throws InterruptedException {
            // given
            Long messageId = 1L;
            Long uploaderId = 100L;
            FileMetadata metadata = createFileMetadata();
            FileType fileType = FileType.DOCUMENT;
            FileSecurity fileSecurity = createFileSecurity();

            FileAttachment fileAttachment = FileAttachment.create(
                messageId,
                uploaderId,
                metadata,
                fileType,
                fileSecurity
            );

            // when
            Thread.sleep(10); // 시간 차이를 위해 잠시 대기
            fileAttachment.update();

            // then
            assertThat(fileAttachment.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("접근자 메서드")
    class AccessorTest {

        @Test
        @DisplayName("모든 접근자 메서드가 올바른 값을 반환한다")
        void should_ReturnCorrectValues_When_AccessorMethodsCalled() {
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
        }
    }
}

