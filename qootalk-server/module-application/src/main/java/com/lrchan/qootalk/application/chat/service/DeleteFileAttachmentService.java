package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.DeleteFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteFileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.in.DeleteFileAttachmentUsecase;
import com.lrchan.qootalk.application.chat.port.out.DeleteFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteFileAttachmentService implements DeleteFileAttachmentUsecase {
    
    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadFileAttachmentPort loadFileAttachmentPort;
    private final DeleteFileAttachmentPort deleteFileAttachmentPort;

    @Override
    public DeleteFileAttachmentQueryResult delete(DeleteFileAttachmentCommand command) {
        
        // 사용자 검증
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
        
        // 채팅방 사용자 검증
        RoomParticipant roomParticipant = loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));
        if (roomParticipant.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_DELETED);
        }

        // 파일 첨부파일 검증
        FileAttachment fileAttachment = loadFileAttachmentPort.findById(command.fileAttachmentId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_FILE_ATTACHMENT_NOT_FOUND));
        if (fileAttachment.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_FILE_ATTACHMENT_DELETED);
        }

        // 권한 검증(Uploader, Owner, or Admin)
        if (!roomParticipant.role().equals(RoomRole.OWNER) && !roomParticipant.role().equals(RoomRole.ADMIN) && !fileAttachment.uploaderId().equals(roomParticipant.userId())) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_ROLE);
        }

        // 파일 첨부파일 삭제
        FileAttachment deletedFileAttachment = deleteFileAttachmentPort.delete(fileAttachment);

        return DeleteFileAttachmentQueryResult.of(deletedFileAttachment);
    }
}
