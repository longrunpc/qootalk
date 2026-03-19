package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.DeleteFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteFileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.out.DeleteFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;

@ExtendWith(MockitoExtension.class)
class DeleteFileAttachmentServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private LoadFileAttachmentPort loadFileAttachmentPort;
    @Mock
    private DeleteFileAttachmentPort deleteFileAttachmentPort;

    @InjectMocks
    private DeleteFileAttachmentService deleteFileAttachmentService;

    @Test
    @DisplayName("업로더는 자신의 첨부파일을 삭제할 수 있다")
    void delete_success_whenRequesterIsUploader() {
        DeleteFileAttachmentCommand command = new DeleteFileAttachmentCommand(1L, 100L, 300L);
        FileAttachment attachment = ChatServiceTestFixtures.attachment(300L, 100L, 200L, 1L, "architecture.pdf", FileType.DOCUMENT);
        FileAttachment deletedAttachment = ChatServiceTestFixtures.deletedAttachment(300L, 100L, 200L, 1L, "architecture.pdf", FileType.DOCUMENT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(10L, 1L, 100L, 200L, RoomRole.MEMBER, true)));
        given(loadFileAttachmentPort.findById(300L)).willReturn(Optional.of(attachment));
        given(deleteFileAttachmentPort.delete(any(FileAttachment.class))).willReturn(deletedAttachment);

        DeleteFileAttachmentQueryResult result = deleteFileAttachmentService.delete(command);

        assertThat(result.id()).isEqualTo(300L);
        assertThat(result.deletedAt()).isNotNull();
        verify(deleteFileAttachmentPort).delete(attachment);
    }

    @Test
    @DisplayName("권한이 없는 참여자는 다른 사용자의 첨부파일을 삭제할 수 없다")
    void delete_fail_whenRequesterHasNoPermission() {
        DeleteFileAttachmentCommand command = new DeleteFileAttachmentCommand(1L, 100L, 300L);
        FileAttachment attachment = ChatServiceTestFixtures.attachment(300L, 100L, 200L, 2L, "architecture.pdf", FileType.DOCUMENT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(10L, 1L, 100L, 200L, RoomRole.MEMBER, true)));
        given(loadFileAttachmentPort.findById(300L)).willReturn(Optional.of(attachment));

        assertThatThrownBy(() -> deleteFileAttachmentService.delete(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_INVALID_ROLE.getMessage());
    }

    @Test
    @DisplayName("이미 삭제된 첨부파일은 다시 삭제할 수 없다")
    void delete_fail_whenAttachmentDeleted() {
        DeleteFileAttachmentCommand command = new DeleteFileAttachmentCommand(1L, 100L, 300L);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(10L, 1L, 100L, 200L, RoomRole.OWNER, true)));
        given(loadFileAttachmentPort.findById(300L))
            .willReturn(Optional.of(ChatServiceTestFixtures.deletedAttachment(300L, 100L, 200L, 1L, "architecture.pdf", FileType.DOCUMENT)));

        assertThatThrownBy(() -> deleteFileAttachmentService.delete(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_FILE_ATTACHMENT_DELETED.getMessage());
    }
}
