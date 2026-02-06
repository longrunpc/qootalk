package com.lrchan.qootalk.infrastructure.aws.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.infrastructure.IntegrationTestSupport;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

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

    @Test
    @DisplayName("S3 파일 업로드 및 삭제 통합 테스트")
    void s3_upload_and_delete_test() {
        // given
        byte[] content = "Hello QooTalk!".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        String fileName = "test_file.txt";
        String path = "test/profile";
        
        StorageResource command = new StorageResource(
                path,
                fileName,
                "text/plain",
                (long) content.length
        );

        String uploadedUri = s3FileStorage.upload(inputStream, command);
    
        assertThat(uploadedUri).contains(fileName);

        String key = uploadedUri.substring(uploadedUri.indexOf(bucketName) + bucketName.length() + 1);

        // when
        s3FileStorage.delete(key);

        // then
        assertThatThrownBy(() -> 
            s3Client.headObject(h -> h.bucket(bucketName).key(key))
        ).isInstanceOf(S3Exception.class)
        .matches(e -> ((S3Exception) e).statusCode() == 404);
    }
}
