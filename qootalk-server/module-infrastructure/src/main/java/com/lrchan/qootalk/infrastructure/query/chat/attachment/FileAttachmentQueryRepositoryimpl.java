package com.lrchan.qootalk.infrastructure.query.chat.attachment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.infrastructure.persistence.chat.attachment.FileAttachmentEntity;
import com.lrchan.qootalk.infrastructure.persistence.chat.attachment.FileAttachmentMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.lrchan.qootalk.infrastructure.persistence.chat.attachment.QFileAttachmentEntity;

@Component
@RequiredArgsConstructor
public class FileAttachmentQueryRepositoryimpl implements FileAttachmentQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    private final QFileAttachmentEntity qFileAttachment = QFileAttachmentEntity.fileAttachmentEntity;
    
    @Override
    public Page<FileAttachmentEntity> findPageByRoomIdAndUploaderIdAndFileType(
            Long roomId, Long uploaderId, FileType fileType, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);

        List<FileAttachmentEntity> entities = queryFactory
            .selectFrom(qFileAttachment)
            .where(
                roomIdEq(roomId), 
                uploaderIdEq(uploaderId), 
                fileTypeEq(fileType), 
                qFileAttachment.deletedAt.isNull()
            )
            .orderBy(qFileAttachment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(qFileAttachment.count())
            .from(qFileAttachment)
            .where(
                roomIdEq(roomId), 
                uploaderIdEq(uploaderId), 
                fileTypeEq(fileType), 
                qFileAttachment.deletedAt.isNull()
            );

        return PageableExecutionUtils.getPage(
            entities, 
            pageable, 
            countQuery::fetchOne
        );
    }

    private BooleanExpression roomIdEq(Long roomId) {
        return roomId != null ? qFileAttachment.roomId.eq(roomId) : null;
    }

    private BooleanExpression uploaderIdEq(Long uploaderId) {
        return uploaderId != null ? qFileAttachment.uploaderId.eq(uploaderId) : null;
    }

    private BooleanExpression fileTypeEq(FileType fileType) {
        return fileType != null ? qFileAttachment.fileType.eq(fileType) : null;
    }
}
