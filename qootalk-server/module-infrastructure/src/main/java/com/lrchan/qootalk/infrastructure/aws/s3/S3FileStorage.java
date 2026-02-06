package com.lrchan.qootalk.infrastructure.aws.s3;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.user.port.out.DeleteProfileImagePort;
import com.lrchan.qootalk.application.user.port.out.UploadProfileImagePort;
import com.lrchan.qootalk.common.exception.InfrastructureException;
import com.lrchan.qootalk.common.storage.port.FileStorage;
import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.infrastructure.persistence.common.error.S3ErrorCode;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage, UploadProfileImagePort, DeleteProfileImagePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    // {버킷이름}/{경로}/{파일이름} 형식의 URI 반환
    @Override
    public String upload(InputStream inputStream, StorageResource command) {
        String fullKey = generateFullKey(command.path(), command.fileName());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(command.contentType())
                .contentLength(command.fileSize())
                .build();
                
            s3Client.putObject(putObjectRequest, 
                RequestBody.fromInputStream(inputStream, command.fileSize()));

            return s3Client.utilities()
                    .getUrl(b -> b.bucket(bucketName).key(fullKey))
                    .toExternalForm();
        }
        catch (Exception e) {
            throw new InfrastructureException(S3ErrorCode.S3_FILE_UPLOAD_FAILED, e);
        }
    }

    private String generateFullKey(String path, String fileName) {
        String normalizedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        String storedFileName = UUID.randomUUID() + "_" + fileName;
        return normalizedPath + "/" + storedFileName;
    }

    @Override
    public void delete(String uri) {
        String key = extractKeyFromUri(uri);

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
        } catch (Exception e) {
            throw new InfrastructureException(S3ErrorCode.S3_FILE_DELETE_FAILED, e);
        }
    }

    private String extractKeyFromUri(String uri) {
        String prefix = endpoint + "/" + bucketName + "/";
        if (uri.contains(prefix)) {
            return uri.substring(uri.indexOf(prefix) + prefix.length());
        }
        return uri; 
    }
}
