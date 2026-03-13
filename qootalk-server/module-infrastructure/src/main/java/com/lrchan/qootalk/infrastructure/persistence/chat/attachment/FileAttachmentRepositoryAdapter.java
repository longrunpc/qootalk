package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachmentRepository;

@Component
public class FileAttachmentRepositoryAdapter implements FileAttachmentRepository, SaveFileAttachmentPort {
    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;

    public FileAttachmentRepositoryAdapter(FileAttachmentJpaRepository fileAttachmentJpaRepository) {
        this.fileAttachmentJpaRepository = fileAttachmentJpaRepository;
    }

    @Override
    public Optional<FileAttachment> findById(Long id) {
        return fileAttachmentJpaRepository.findById(Objects.requireNonNull(id)).map(FileAttachmentMapper::toDomain);
    }

    @Override
    public Optional<FileAttachment> findByMessageId(Long messageId) {
        return fileAttachmentJpaRepository.findByMessageId(messageId).map(FileAttachmentMapper::toDomain);
    }

    @Override
    public FileAttachment save(FileAttachment fileAttachment) {
        FileAttachmentEntity fileAttachmentEntity = FileAttachmentMapper.toEntity(fileAttachment);
        FileAttachmentEntity savedEntity = fileAttachmentJpaRepository.save(Objects.requireNonNull(fileAttachmentEntity));
        return FileAttachmentMapper.toDomain(savedEntity);
    }
}
