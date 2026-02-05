package com.lrchan.qootalk.infrastructure.aws.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileUploadCommand;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;

import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileSize;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@DisplayName("S3FileStorage 테스트")
public class S3FileStorageTest extends IntegrationTestSupport {

    @Autowired
    private S3FileStorage s3FileStorage;

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
        assertThat(metadata.storagePath()).isEqualTo(new Path("s3/chats/attachments/"));
        assertThat(metadata.storageType()).isEqualTo(StorageType.S3);
    }
}
