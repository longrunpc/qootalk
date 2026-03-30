package com.lrchan.qootalk.application.chat.service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatHistoriesCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatHistoryQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatHistoriesUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.response.SliceResponse;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoadChatHistoriesService implements LoadChatHistoriesUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadChatRoomPort loadChatRoomPort;
    private final LoadRoomParticipantPort loadRoomParticipantPort;
    private final LoadMessagePort loadMessagePort;

    @Override
    public SliceResponse<ChatHistoryQueryResult> load(LoadChatHistoriesCommand command) {
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        ChatRoom chatRoom = loadChatRoomPort.findById(command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.deletedAt() != null) {
            throw new DomainException(ChatErrorCode.CHAT_ROOM_DELETED);
        }

        loadRoomParticipantPort.findByUserIdAndRoomId(command.requesterId(), command.roomId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND));

        Slice<com.lrchan.qootalk.domain.chat.message.Message> messages = loadMessagePort.findSliceByRoomId(
            command.roomId(),
            command.fromMessageId(),
            command.page(),
            command.size()
        );

        return SliceResponse.of(
            messages.getContent().stream().map(ChatHistoryQueryResult::of).toList(),
            messages.getNumber(),
            messages.getSize(),
            messages.hasNext()
        );
    }
}
