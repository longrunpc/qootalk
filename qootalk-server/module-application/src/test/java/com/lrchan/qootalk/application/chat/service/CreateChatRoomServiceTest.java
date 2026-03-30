package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.lrchan.qootalk.application.chat.dto.command.CreateChatRoomCommand;
import com.lrchan.qootalk.application.chat.dto.result.CreateChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.out.SaveChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.RoomType;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

@ExtendWith(MockitoExtension.class)
class CreateChatRoomServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private SaveChatRoomPort saveChatRoomPort;
    @Mock
    private SaveRoomParticipantPort saveRoomParticipantPort;
    @Mock
    private SaveMessagePort saveMessagePort;

    @InjectMocks
    private CreateChatRoomService createChatRoomService;

    @Test
    @DisplayName("채팅방 생성에 성공하면 시스템 메시지와 참여자가 함께 저장된다")
    void create_success() {
        CreateChatRoomCommand command = new CreateChatRoomCommand(1L, "backend", RoomType.GROUP, List.of(2L, 3L), false);
        User requester = ChatServiceTestFixtures.activeUser(1L);
        User participant1 = ChatServiceTestFixtures.activeUser(2L);
        User participant2 = ChatServiceTestFixtures.activeUser(3L);
        ChatRoom savedRoom = ChatServiceTestFixtures.activeRoom(100L, 1L);
        Message savedMessage = ChatServiceTestFixtures.message(200L, 100L, null, "채팅방을 생성했습니다.", MessageType.SYSTEM);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(requester));
        given(loadUserPort.findById(2L)).willReturn(Optional.of(participant1));
        given(loadUserPort.findById(3L)).willReturn(Optional.of(participant2));
        given(saveChatRoomPort.save(any(ChatRoom.class))).willReturn(savedRoom);
        given(saveMessagePort.save(any(Message.class))).willReturn(savedMessage);

        CreateChatRoomQueryResult result = createChatRoomService.create(command);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.createdBy()).isEqualTo(1L);
        assertThat(result.participantCount()).isEqualTo(3);

        ArgumentCaptor<RoomParticipant> participantCaptor = ArgumentCaptor.forClass(RoomParticipant.class);
        verify(saveRoomParticipantPort, times(3)).save(participantCaptor.capture());
        assertThat(participantCaptor.getAllValues())
            .extracting(RoomParticipant::userId)
            .containsExactly(1L, 2L, 3L);
        assertThat(participantCaptor.getAllValues())
            .allMatch(participant -> !participant.notificationEnabled());
    }

    @Test
    @DisplayName("요청 사용자를 찾을 수 없으면 예외가 발생한다")
    void create_fail_whenRequesterNotFound() {
        CreateChatRoomCommand command = new CreateChatRoomCommand(1L, "backend", RoomType.GROUP, List.of(2L), true);
        given(loadUserPort.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> createChatRoomService.create(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("삭제된 참여자가 포함되면 예외가 발생한다")
    void create_fail_whenParticipantDeleted() {
        CreateChatRoomCommand command = new CreateChatRoomCommand(1L, "backend", RoomType.GROUP, List.of(2L), true);
        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadUserPort.findById(2L)).willReturn(Optional.of(ChatServiceTestFixtures.deletedUser(2L)));

        assertThatThrownBy(() -> createChatRoomService.create(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_DELETED.getMessage());
    }
}
