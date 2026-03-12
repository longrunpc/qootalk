package com.lrchan.qootalk.application.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.DeleteChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.in.DeleteChatRoomUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
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
public class DeleteChatRoomService implements DeleteChatRoomUsecase {
    
    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final SaveChatRoomPort saveChatRoomPort;
    private final SaveRoomParticipantPort saveRoomParticipantPort;

    @Override
    public DeleteChatRoomQueryResult delete(DeleteChatRoomCommand command) {
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

        // 채팅방 권한 검증
        if (!roomParticipant.role().equals(RoomRole.OWNER) && !roomParticipant.role().equals(RoomRole.ADMIN)) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_ROLE);
        }

        // 채팅방 삭제
        chatRoom.delete();
        saveChatRoomPort.save(chatRoom);

        // 채팅방 참여자 삭제
        List<RoomParticipant> roomParticipants = loadRoomParticipantPort.findActiveByRoomId(command.roomId());
        for (RoomParticipant participant : roomParticipants) {
            participant.delete();
            saveRoomParticipantPort.save(participant);
        }

        return DeleteChatRoomQueryResult.of(chatRoom);
    }

}
