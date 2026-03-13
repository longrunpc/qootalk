package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.UploadFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.in.UploadFileAttachmentUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.UploadFilePort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.vo.ContentType;
import com.lrchan.qootalk.domain.chat.vo.FileMetadata;
import com.lrchan.qootalk.domain.chat.vo.FileName;
import com.lrchan.qootalk.domain.chat.vo.FileSecurity;
import com.lrchan.qootalk.domain.chat.vo.Path;
import com.lrchan.qootalk.domain.chat.vo.StorageType;
import com.lrchan.qootalk.domain.chat.vo.FileSize;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadFileAttachmentService implements UploadFileAttachmentUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final UploadFilePort uploadFilePort;
    private final SaveFileAttachmentPort saveFileAttachmentPort;

    @Override
    public FileAttachmentQueryResult upload(UploadFileAttachmentCommand command) {
        // 유저 검증
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        // 채팅방 검증
        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_DELETED);
        }
        
        // 채팅방 참여자 검증
        RoomParticipant roomParticipant = loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));
        if (roomParticipant.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_DELETED);
        }

        // 파일 업로드
        String path = "chat/" + command.roomId() + "/attachments/" + command.messageId() + "/";
        StorageResource resource = new StorageResource(
            path,
            command.originalFileName(),
            command.contentType(),
            command.fileSize());
        String uri = uploadFilePort.upload(command.inputStream(), resource);

        // 파일 저장
        FileMetadata metadata = new FileMetadata(
            new FileName(command.originalFileName()),
            new FileName(uri.substring(uri.lastIndexOf('/') + 1)),
            new ContentType(command.contentType()),
            new FileSize(command.fileSize()),
            new Path(path),
            StorageType.LOCAL);
        FileAttachment fileAttachment = FileAttachment.create(
            command.roomId(), command.requesterId(), metadata, FileType.fromContentType(new ContentType(command.contentType())), FileSecurity.defaultPrivate());

        FileAttachment savedFileAttachment = saveFileAttachmentPort.save(fileAttachment);
        return FileAttachmentQueryResult.of(savedFileAttachment);
    }
}
