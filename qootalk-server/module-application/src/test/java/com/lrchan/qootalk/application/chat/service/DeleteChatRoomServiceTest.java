package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.DeleteChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteChatRoomQueryResult;
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

@ExtendWith(MockitoExtension.class)
class DeleteChatRoomServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private SaveChatRoomPort saveChatRoomPort;
    @Mock
    private SaveRoomParticipantPort saveRoomParticipantPort;

    @InjectMocks
    private DeleteChatRoomService deleteChatRoomService;

    @Test
    @DisplayName("OWNER가 채팅방을 삭제하면 방과 참여자가 모두 삭제된다")
    void delete_success() {
        DeleteChatRoomCommand command = new DeleteChatRoomCommand(1L, 100L);
        ChatRoom room = ChatServiceTestFixtures.activeRoom(100L, 1L);
        RoomParticipant owner = ChatServiceTestFixtures.participant(1L, 1L, 100L, 200L, RoomRole.OWNER, true);
        RoomParticipant member = ChatServiceTestFixtures.participant(2L, 2L, 100L, 200L, RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(room));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.of(owner));
        given(loadRoomParticipantPort.findActiveByRoomId(100L)).willReturn(List.of(owner, member));

        DeleteChatRoomQueryResult result = deleteChatRoomService.delete(command);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.deletedAt()).isNotNull();
        verify(saveChatRoomPort).save(room);

        ArgumentCaptor<RoomParticipant> captor = ArgumentCaptor.forClass(RoomParticipant.class);
        verify(saveRoomParticipantPort, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).allMatch(RoomParticipant::isDeleted);
    }

    @Test
    @DisplayName("권한이 없는 참여자는 채팅방을 삭제할 수 없다")
    void delete_fail_whenInvalidRole() {
        DeleteChatRoomCommand command = new DeleteChatRoomCommand(1L, 100L);
        ChatRoom room = ChatServiceTestFixtures.activeRoom(100L, 1L);
        RoomParticipant member = ChatServiceTestFixtures.participant(1L, 1L, 100L, 200L, RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(room));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.of(member));

        assertThatThrownBy(() -> deleteChatRoomService.delete(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_ROLE.getMessage());
    }
}
