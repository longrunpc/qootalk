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

import com.lrchan.qootalk.application.chat.dto.command.SendMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.SendMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.PublishChatMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

@ExtendWith(MockitoExtension.class)
class SendMessageServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private LoadMessagePort loadMessagePort;
    @Mock
    private LoadFileAttachmentPort loadFileAttachmentPort;
    @Mock
    private SaveMessagePort saveMessagePort;
    @Mock
    private SaveFileAttachmentPort saveFileAttachmentPort;
    @Mock
    private SaveRoomParticipantPort saveRoomParticipantPort;
    @Mock
    private PublishChatMessagePort publishChatMessagePort;

    @InjectMocks
    private SendMessageService sendMessageService;

    @Test
    @DisplayName("메시지 전송에 성공하면 메시지를 저장하고 발신자의 읽음 위치를 갱신한다")
    void send_success() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, "안녕하세요", MessageType.TEXT, List.of(2L), null, List.of());
        RoomParticipant participant = ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true);
        Message savedMessage = ChatServiceTestFixtures.message(20L, 10L, 1L, "안녕하세요", MessageType.TEXT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.of(participant));
        given(loadRoomParticipantPort.findActiveByRoomId(10L)).willReturn(List.of(
            ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true),
            ChatServiceTestFixtures.participant(101L, 2L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)
        ));
        given(saveMessagePort.save(any(Message.class))).willReturn(savedMessage);

        SendMessageQueryResult result = sendMessageService.send(command);

        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.roomId()).isEqualTo(10L);
        assertThat(result.senderId()).isEqualTo(1L);
        assertThat(result.content()).isEqualTo("안녕하세요");
        assertThat(result.attachmentIds()).isEmpty();

        ArgumentCaptor<RoomParticipant> participantCaptor = ArgumentCaptor.forClass(RoomParticipant.class);
        verify(saveRoomParticipantPort).save(participantCaptor.capture());
        assertThat(participantCaptor.getValue().lastReadMessageId()).isEqualTo(20L);
        verify(publishChatMessagePort).publish(any());
    }

    @Test
    @DisplayName("첨부파일 메시지 전송에 성공하면 첨부파일을 새 메시지에 연결한다")
    void send_success_withAttachments() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, null, MessageType.FILE, List.of(), null, List.of(31L, 32L));
        RoomParticipant participant = ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true);
        Message savedMessage = ChatServiceTestFixtures.message(20L, 10L, 1L, null, MessageType.FILE);
        FileAttachment attachment1 = ChatServiceTestFixtures.attachment(31L, 10L, 1L, 1L, "doc-1.pdf", com.lrchan.qootalk.domain.chat.attachment.FileType.DOCUMENT);
        FileAttachment attachment2 = ChatServiceTestFixtures.attachment(32L, 10L, 1L, 1L, "doc-2.pdf", com.lrchan.qootalk.domain.chat.attachment.FileType.DOCUMENT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L)).willReturn(Optional.of(participant));
        given(saveMessagePort.save(any(Message.class))).willReturn(savedMessage);
        given(loadFileAttachmentPort.findById(31L)).willReturn(Optional.of(attachment1));
        given(loadFileAttachmentPort.findById(32L)).willReturn(Optional.of(attachment2));

        SendMessageQueryResult result = sendMessageService.send(command);

        assertThat(result.attachmentIds()).containsExactly(31L, 32L);
        verify(saveFileAttachmentPort, times(2)).save(any(FileAttachment.class));
        verify(publishChatMessagePort).publish(any());
    }

    @Test
    @DisplayName("메시지 내용과 첨부파일이 모두 없으면 예외가 발생한다")
    void send_fail_whenPayloadEmpty() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, "   ", MessageType.TEXT, List.of(), null, List.of());

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_EMPTY_PAYLOAD.getMessage());
    }

    @Test
    @DisplayName("사용자는 SYSTEM 메시지를 전송할 수 없다")
    void send_fail_whenMessageTypeNotAllowed() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, "시스템 공지", MessageType.SYSTEM, List.of(), null, List.of());

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_TYPE_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("다른 채팅방의 부모 메시지로 답장을 보내면 예외가 발생한다")
    void send_fail_whenParentMessageBelongsToAnotherRoom() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, "답장", MessageType.REPLY, List.of(), 99L, List.of());

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));
        given(loadMessagePort.findById(99L)).willReturn(Optional.of(ChatServiceTestFixtures.message(99L, 20L, 2L, "다른 방 메시지", MessageType.TEXT)));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_INVALID_PARENT.getMessage());
    }

    @Test
    @DisplayName("채팅방 참여자가 아닌 사용자를 멘션하면 예외가 발생한다")
    void send_fail_whenMentionTargetNotParticipant() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, "멘션", MessageType.TEXT, List.of(99L), null, List.of());

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));
        given(loadRoomParticipantPort.findActiveByRoomId(10L)).willReturn(List.of(
            ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true),
            ChatServiceTestFixtures.participant(101L, 2L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)
        ));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_MENTION_TARGET_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("본인이 업로드하지 않은 첨부파일은 메시지에 포함할 수 없다")
    void send_fail_whenAttachmentOwnerMismatch() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, null, MessageType.FILE, List.of(), null, List.of(31L));
        FileAttachment attachment = ChatServiceTestFixtures.attachment(31L, 10L, 1L, 2L, "doc.pdf", com.lrchan.qootalk.domain.chat.attachment.FileType.DOCUMENT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));
        given(saveMessagePort.save(any(Message.class))).willReturn(ChatServiceTestFixtures.message(20L, 10L, 1L, null, MessageType.FILE));
        given(loadFileAttachmentPort.findById(31L)).willReturn(Optional.of(attachment));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_ATTACHMENT_OWNER_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("중복된 첨부파일 ID는 메시지에 포함할 수 없다")
    void send_fail_whenAttachmentIdsDuplicated() {
        SendMessageCommand command = new SendMessageCommand(1L, 10L, null, MessageType.FILE, List.of(), null, List.of(31L, 31L));

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(10L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(10L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 10L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(100L, 1L, 10L, 1L, com.lrchan.qootalk.domain.chat.participant.RoomRole.MEMBER, true)));

        assertThatThrownBy(() -> sendMessageService.send(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_DUPLICATE_ATTACHMENT.getMessage());
    }
}
