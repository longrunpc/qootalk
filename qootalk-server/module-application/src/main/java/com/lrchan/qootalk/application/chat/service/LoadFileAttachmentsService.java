package com.lrchan.qootalk.application.chat.service;

import com.lrchan.qootalk.application.chat.dto.command.LoadFileAttachmentsCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadFileAttachmentsUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.common.response.PagedResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service  
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoadFileAttachmentsService implements LoadFileAttachmentsUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadFileAttachmentPort loadFileAttachmentPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    
    @Override
    public PagedResponse<FileAttachmentQueryResult> load(LoadFileAttachmentsCommand command) {
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
        
        // 파일 조회
        Page<FileAttachment> fileAttachments = loadFileAttachmentPort.findPageByRoomIdAndUploaderIdAndFileType(command.roomId(), command.uploaderId(), command.fileType(), command.page(), command.size());
        
        return PagedResponse.of(fileAttachments.map(FileAttachmentQueryResult::of).toList(), fileAttachments.getNumber(), fileAttachments.getSize(), fileAttachments.getTotalElements(), fileAttachments.getTotalPages());
    }
}
