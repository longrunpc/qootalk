package com.lrchan.qootalk.infrastructure.aws.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileUploadCommand;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileSize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@DisplayName("S3FileStorage 테스트")
public class S3FileStorageTest extends IntegrationTestSupport {

    @Autowired
    private S3FileStorage s3FileStorage;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.storage.path}")
    private String storagePath;

    @Test
    @DisplayName("파일 저장 테스트")
    void should_uploadFile_when_validFile() {
        // given
        byte[] content = "This is a test file".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        FileUploadCommand command = new FileUploadCommand(
                new FileName("test.txt"),
                new FileSize((long) content.length),
                new ContentType("text/plain"));

        // when
        FileMetadata metadata = s3FileStorage.upload(inputStream, command);

        // then
        assertThat(metadata.originalFileName()).isEqualTo(new FileName("test.txt"));
        assertThat(metadata.storedFileName()).isNotNull();
        assertThat(metadata.contentType()).isEqualTo(new ContentType("text/plain"));
        assertThat(metadata.fileSize()).isEqualTo(new FileSize((long) content.length));
        assertThat(metadata.storagePath()).isEqualTo(new Path(storagePath));
        assertThat(metadata.storageType()).isEqualTo(StorageType.S3);
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    void should_deleteFile_when_validFile() {
        // given
        byte[] content = "This is a test file".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        FileUploadCommand command = new FileUploadCommand(
                new FileName("test.txt"),
                new FileSize((long) content.length),
                new ContentType("text/plain"));

        FileMetadata metadata = s3FileStorage.upload(inputStream, command);

        // when & then
        s3FileStorage.delete(metadata);

        assertThatThrownBy(() -> 
            s3Client.headObject(br -> br.bucket(bucketName).key(metadata.storagePath().value() + metadata.storedFileName().value()))
        ).isInstanceOf(NoSuchKeyException.class);
    }
}
