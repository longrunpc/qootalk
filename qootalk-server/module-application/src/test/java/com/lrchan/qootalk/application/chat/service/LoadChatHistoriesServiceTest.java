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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatHistoriesCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatHistoryQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.response.SliceResponse;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.MessageType;

@ExtendWith(MockitoExtension.class)
class LoadChatHistoriesServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private LoadMessagePort loadMessagePort;

    @InjectMocks
    private LoadChatHistoriesService loadChatHistoriesService;

    @Test
    @DisplayName("채팅 이력은 Slice 형태로 조회된다")
    void load_success() {
        LoadChatHistoriesCommand command = new LoadChatHistoriesCommand(1L, 10L, null, 0, 2);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));
        given(loadMessagePort.findSliceByRoomId(10L, null, 0, 2)).willReturn(new SliceImpl<>(
            List.of(
                ChatServiceTestFixtures.message(5L, 10L, 1L, "latest", MessageType.TEXT),
                ChatServiceTestFixtures.message(4L, 10L, 2L, "previous", MessageType.TEXT)
            ),
            PageRequest.of(0, 2),
            true
        ));

        SliceResponse<ChatHistoryQueryResult> result = loadChatHistoriesService.load(command);

        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).content()).isEqualTo("latest");
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("참여하지 않은 사용자는 채팅 이력을 조회할 수 없다")
    void load_fail_whenNotParticipant() {
        LoadChatHistoriesCommand command = new LoadChatHistoriesCommand(1L, 10L, null, 0, 20);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> loadChatHistoriesService.load(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND.getMessage());
    }
}
