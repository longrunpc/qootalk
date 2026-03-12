package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomsCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

@ExtendWith(MockitoExtension.class)
class LoadChatRoomsServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadMessagePort loadMessagePort;

    @InjectMocks
    private LoadChatRoomsService loadChatRoomsService;

    @Test
    @DisplayName("채팅방 목록 조회에 성공하면 페이징 응답을 반환한다")
    void load_success() {
        LoadChatRoomsCommand command = new LoadChatRoomsCommand(1L, 0, 10);
        RoomParticipant roomParticipant = ChatServiceTestFixtures.participant(1L, 1L, 100L, 200L, RoomRole.OWNER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadRoomParticipantPort.findActivePageByUserId(1L, 0, 10))
            .willReturn(PagedResponse.of(java.util.List.of(roomParticipant), 0, 10, 1, 1));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadMessagePort.findById(200L)).willReturn(Optional.of(ChatServiceTestFixtures.message(200L, 100L, 1L, "hello", MessageType.TEXT)));
        given(loadMessagePort.countByRoomIdAndIdAfter(100L, 200L)).willReturn(3L);

        PagedResponse<ChatRoomQueryResult> result = loadChatRoomsService.load(command);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).lastMessage()).isEqualTo("hello");
        assertThat(result.content().get(0).unreadCount()).isEqualTo(3);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자를 찾을 수 없으면 채팅방 목록 조회에 실패한다")
    void load_fail_whenUserNotFound() {
        LoadChatRoomsCommand command = new LoadChatRoomsCommand(1L, 0, 10);
        given(loadUserPort.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loadChatRoomsService.load(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }
}
