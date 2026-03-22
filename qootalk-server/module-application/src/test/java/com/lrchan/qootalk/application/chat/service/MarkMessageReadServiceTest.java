package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.MarkMessageReadCommand;
import com.lrchan.qootalk.application.chat.dto.result.ReadReceiptQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

@ExtendWith(MockitoExtension.class)
class MarkMessageReadServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private LoadMessagePort loadMessagePort;
    @Mock
    private SaveRoomParticipantPort saveRoomParticipantPort;

    @InjectMocks
    private MarkMessageReadService markMessageReadService;

    @Test
    @DisplayName("더 최신 메시지를 읽음 처리하면 마지막 읽음 메시지 ID가 갱신된다")
    void mark_success() {
        MarkMessageReadCommand command = new MarkMessageReadCommand(1L, 10L, 20L);
        RoomParticipant participant = ChatServiceTestFixtures.participant(100L, 1L, 10L, 10L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.of(participant));
        given(loadMessagePort.findById(20L)).willReturn(Optional.of(ChatServiceTestFixtures.message(20L, 10L, 2L, "message", MessageType.TEXT)));

        ReadReceiptQueryResult result = markMessageReadService.mark(command);

        assertThat(result.updated()).isTrue();
        assertThat(result.lastReadMessageId()).isEqualTo(20L);
        verify(saveRoomParticipantPort).save(participant);
    }

    @Test
    @DisplayName("이미 읽은 메시지 이하를 읽음 처리하면 갱신하지 않는다")
    void mark_success_whenAlreadyRead() {
        MarkMessageReadCommand command = new MarkMessageReadCommand(1L, 10L, 10L);
        RoomParticipant participant = ChatServiceTestFixtures.participant(100L, 1L, 10L, 10L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.of(participant));
        given(loadMessagePort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.message(10L, 10L, 2L, "message", MessageType.TEXT)));

        ReadReceiptQueryResult result = markMessageReadService.mark(command);

        assertThat(result.updated()).isFalse();
        assertThat(result.lastReadMessageId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("다른 채팅방 메시지를 읽음 처리하면 예외가 발생한다")
    void mark_fail_whenMessageBelongsToAnotherRoom() {
        MarkMessageReadCommand command = new MarkMessageReadCommand(1L, 10L, 20L);
        RoomParticipant participant = ChatServiceTestFixtures.participant(100L, 1L, 10L, 10L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.of(participant));
        given(loadMessagePort.findById(20L)).willReturn(Optional.of(ChatServiceTestFixtures.message(20L, 99L, 2L, "message", MessageType.TEXT)));

        assertThatThrownBy(() -> markMessageReadService.mark(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_LAST_READ_MESSAGE_ID.getMessage());
    }
}
