package com.lrchan.qootalk.infrastructure.aws.s3;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.ContentType;
import com.lrchan.qootalk.domain.chat.attachment.FileStorage;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileUploadCommand;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public FileMetadata upload(InputStream inputStream, FileUploadCommand command) {
        String storedFileName = UUID.randomUUID() + "_" + command.originalFileName().value();
        String storagePath = "s3/chats/attachments/";
        String fullKey = storagePath + storedFileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(command.contentType().value())
                .contentLength(command.fileSize().value())
                .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, command.fileSize().value()));

            return new FileMetadata(
                command.originalFileName(),
                new FileName(storedFileName),
                command.contentType(),
                command.fileSize(),
                new Path(storagePath),
                StorageType.S3
            );
        }
        catch (Exception e) {
            throw new InfrastructureException("Failed to upload file to S3", e);
        }
    }

    @Override
    public void delete(FileMetadata metadata) {
        String key = metadata.storagePath().value() + metadata.storedFileName().value();
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());
    }
}
