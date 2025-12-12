package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileMetadata VO 테스트")
class FileMetadataTest {

    private FileName createFileName(String name) {
        return new FileName(name);
    }

    private ContentType createContentType(String type) {
        return new ContentType(type);
    }

    private FileSize createFileSize(Long size) {
        return new FileSize(size);
    }

    private Path createPath(String path) {
        return new Path(path);
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 메타데이터로 생성할 수 있다")
        void should_CreateFileMetadata_When_ValidData() {
            // given
            FileName originalFileName = createFileName("original.pdf");
            FileName storedFileName = createFileName("stored-uuid.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(1024L);
            Path storagePath = createPath("uploads/files/");
            StorageType storageType = StorageType.LOCAL;

            // when
            FileMetadata metadata = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            // then
            assertThat(metadata.originalFileName()).isEqualTo(originalFileName);
            assertThat(metadata.storedFileName()).isEqualTo(storedFileName);
            assertThat(metadata.contentType()).isEqualTo(contentType);
            assertThat(metadata.fileSize()).isEqualTo(fileSize);
            assertThat(metadata.storagePath()).isEqualTo(storagePath);
            assertThat(metadata.storageType()).isEqualTo(storageType);
        }

        @Test
        @DisplayName("S3 스토리지 타입으로 생성할 수 있다")
        void should_CreateFileMetadata_When_S3Storage() {
            // given
            FileName originalFileName = createFileName("image.jpg");
            FileName storedFileName = createFileName("stored-uuid.jpg");
            ContentType contentType = createContentType("image/jpeg");
            FileSize fileSize = createFileSize(2048L);
            Path storagePath = createPath("s3/bucket/files/");
            StorageType storageType = StorageType.S3;

            // when
            FileMetadata metadata = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            // then
            assertThat(metadata.storageType()).isEqualTo(StorageType.S3);
        }

        @Test
        @DisplayName("TEMP 스토리지 타입으로 생성할 수 있다")
        void should_CreateFileMetadata_When_TempStorage() {
            // given
            FileName originalFileName = createFileName("temp.pdf");
            FileName storedFileName = createFileName("temp-uuid.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(512L);
            Path storagePath = createPath("system/tmp/");
            StorageType storageType = StorageType.TEMP;

            // when
            FileMetadata metadata = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            // then
            assertThat(metadata.storageType()).isEqualTo(StorageType.TEMP);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값이 포함되면 예외가 발생한다")
        void should_ThrowException_When_ContainsNull() {
            // given
            FileName originalFileName = createFileName("original.pdf");
            FileName storedFileName = createFileName("stored.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(1024L);
            Path storagePath = createPath("uploads/files/");
            StorageType storageType = StorageType.LOCAL;

            // when & then
            assertThatThrownBy(() -> new FileMetadata(null, storedFileName, contentType, fileSize, storagePath, storageType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("originalFileName");

            assertThatThrownBy(() -> new FileMetadata(originalFileName, null, contentType, fileSize, storagePath, storageType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("storedFileName");

            assertThatThrownBy(() -> new FileMetadata(originalFileName, storedFileName, null, fileSize, storagePath, storageType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("contentType");

            assertThatThrownBy(() -> new FileMetadata(originalFileName, storedFileName, contentType, null, storagePath, storageType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fileSize");

            assertThatThrownBy(() -> new FileMetadata(originalFileName, storedFileName, contentType, fileSize, null, storageType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("storagePath");

            assertThatThrownBy(() -> new FileMetadata(originalFileName, storedFileName, contentType, fileSize, storagePath, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("storageType");
        }

        @Test
        @DisplayName("TEMP 스토리지가 system/tmp/ 경로가 아니면 예외가 발생한다")
        void should_ThrowException_When_TempStorageWithInvalidPath() {
            // given
            FileName originalFileName = createFileName("temp.pdf");
            FileName storedFileName = createFileName("temp-uuid.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(512L);
            Path invalidPath = createPath("uploads/files/");
            StorageType storageType = StorageType.TEMP;

            // when & then
            assertThatThrownBy(() -> new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                invalidPath,
                storageType
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TEMP storage must use path starting and ending with 'system/tmp/'");
        }

        @Test
        @DisplayName("LOCAL 스토리지가 s3 경로를 사용하면 예외가 발생한다")
        void should_ThrowException_When_LocalStorageWithS3Path() {
            // given
            FileName originalFileName = createFileName("file.pdf");
            FileName storedFileName = createFileName("stored-uuid.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(1024L);
            Path s3Path = createPath("s3/bucket/files/");
            StorageType storageType = StorageType.LOCAL;

            // when & then
            assertThatThrownBy(() -> new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                s3Path,
                storageType
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("LOCAL storage cannot use remote path");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 FileMetadata는 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            FileName originalFileName = createFileName("original.pdf");
            FileName storedFileName = createFileName("stored.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(1024L);
            Path storagePath = createPath("uploads/files/");
            StorageType storageType = StorageType.LOCAL;

            FileMetadata metadata1 = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            FileMetadata metadata2 = new FileMetadata(
                originalFileName,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            // when & then
            assertThat(metadata1).isEqualTo(metadata2);
            assertThat(metadata1.hashCode()).isEqualTo(metadata2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 FileMetadata는 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            FileName originalFileName1 = createFileName("original1.pdf");
            FileName originalFileName2 = createFileName("original2.pdf");
            FileName storedFileName = createFileName("stored.pdf");
            ContentType contentType = createContentType("application/pdf");
            FileSize fileSize = createFileSize(1024L);
            Path storagePath = createPath("uploads/files/");
            StorageType storageType = StorageType.LOCAL;

            FileMetadata metadata1 = new FileMetadata(
                originalFileName1,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            FileMetadata metadata2 = new FileMetadata(
                originalFileName2,
                storedFileName,
                contentType,
                fileSize,
                storagePath,
                storageType
            );

            // when & then
            assertThat(metadata1).isNotEqualTo(metadata2);
        }
    }
}

