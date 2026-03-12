package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.UpdateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;

@ExtendWith(MockitoExtension.class)
class UpdateChatRoomServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private SaveChatRoomPort saveChatRoomPort;

    @InjectMocks
    private UpdateChatRoomService updateChatRoomService;

    @Test
    @DisplayName("채팅방 수정에 성공하면 변경된 이름이 반환된다")
    void update_success() {
        UpdateChatRoomCommand command = new UpdateChatRoomCommand(1L, 100L, "renamed-room");
        ChatRoom room = ChatServiceTestFixtures.activeRoom(100L, 1L);
        var participant = ChatServiceTestFixtures.participant(1L, 1L, 100L, 200L, RoomRole.OWNER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(room));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.of(participant));
        given(saveChatRoomPort.save(any(ChatRoom.class))).willAnswer(invocation -> invocation.getArgument(0));

        UpdateChatRoomQueryResult result = updateChatRoomService.update(command);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.roomName()).isEqualTo("renamed-room");
    }

    @Test
    @DisplayName("참여자가 아니면 채팅방 수정에 실패한다")
    void update_fail_whenParticipantNotFound() {
        UpdateChatRoomCommand command = new UpdateChatRoomCommand(1L, 100L, "renamed-room");

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateChatRoomService.update(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND.getMessage());
    }
}
