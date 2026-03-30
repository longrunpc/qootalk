package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomDetailCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;

@ExtendWith(MockitoExtension.class)
class LoadChatRoomDetailServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;

    @InjectMocks
    private LoadChatRoomDetailService loadChatRoomDetailService;

    @Test
    @DisplayName("채팅방 상세 조회에 성공하면 참여자 목록과 알림 설정을 반환한다")
    void load_success() {
        LoadChatRoomDetailCommand command = new LoadChatRoomDetailCommand(1L, 100L);
        var requester = ChatServiceTestFixtures.participant(1L, 1L, 100L, 200L, RoomRole.OWNER, false);
        var member = ChatServiceTestFixtures.participant(2L, 2L, 100L, 200L, RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.of(requester));
        given(loadRoomParticipantPort.findActiveByRoomId(100L)).willReturn(List.of(requester, member));

        ChatRoomDetailQueryResult result = loadChatRoomDetailService.load(command);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.notificationEnabled()).isFalse();
        assertThat(result.participants()).hasSize(2);
    }

    @Test
    @DisplayName("참여자가 아니면 채팅방 상세 조회에 실패한다")
    void load_fail_whenParticipantNotFound() {
        LoadChatRoomDetailCommand command = new LoadChatRoomDetailCommand(1L, 100L);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loadChatRoomDetailService.load(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND.getMessage());
    }
}
