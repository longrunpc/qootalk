package com.lrchan.qootalk.infrastructure.persistence.chat.attachment;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.port.out.DeleteFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachmentRepository;
import com.lrchan.qootalk.domain.chat.attachment.FileType;

@Component
public class FileAttachmentRepositoryAdapter implements FileAttachmentRepository, SaveFileAttachmentPort, LoadFileAttachmentPort, DeleteFileAttachmentPort {
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

    @Override
    public Page<FileAttachment> findPageByRoomIdAndUploaderIdAndFileType(Long roomId, Long uploaderId, FileType fileType, int page, int size) {

        Page<FileAttachmentEntity> fileAttachmentEntities = fileAttachmentJpaRepository.findPageByRoomIdAndUploaderIdAndFileType(roomId, uploaderId, fileType, page, size);
        return fileAttachmentEntities.map(FileAttachmentMapper::toDomain);
    }

    @Override
    public FileAttachment delete(FileAttachment fileAttachment) {
        FileAttachmentEntity fileAttachmentEntity = FileAttachmentMapper.toEntity(fileAttachment);
        FileAttachmentEntity deletedEntity = fileAttachmentJpaRepository.save(fileAttachmentEntity);
        return FileAttachmentMapper.toDomain(deletedEntity);
    }
}
