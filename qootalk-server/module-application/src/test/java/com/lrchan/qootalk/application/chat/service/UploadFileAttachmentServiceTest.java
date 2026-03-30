package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.UploadFileAttachmentCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.chat.port.out.SaveFileAttachmentPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.UploadFilePort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.attachment.FileAttachment;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

@ExtendWith(MockitoExtension.class)
class UploadFileAttachmentServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private UploadFilePort uploadFilePort;
    @Mock
    private SaveFileAttachmentPort saveFileAttachmentPort;

    @InjectMocks
    private UploadFileAttachmentService uploadFileAttachmentService;

    @Test
    @DisplayName("첨부파일 업로드에 성공하면 메시지 ID와 저장 경로가 함께 저장된다")
    void upload_success() {
        UploadFileAttachmentCommand command = new UploadFileAttachmentCommand(
            1L,
            100L,
            200L,
            new ByteArrayInputStream("pdf".getBytes(StandardCharsets.UTF_8)),
            "architecture.pdf",
            "application/pdf",
            1024L
        );
        FileAttachment savedAttachment = ChatServiceTestFixtures.attachment(300L, 100L, 200L, 1L, "architecture.pdf", FileType.DOCUMENT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(10L, 1L, 100L, 200L, RoomRole.MEMBER, true)));
        given(uploadFilePort.upload(any(), any()))
            .willReturn("http://localhost:4566/uploads/chat/100/attachments/200/stored-architecture.pdf");
        given(saveFileAttachmentPort.save(any(FileAttachment.class))).willReturn(savedAttachment);

        FileAttachmentQueryResult result = uploadFileAttachmentService.upload(command);

        assertThat(result.id()).isEqualTo(300L);
        assertThat(result.messageId()).isEqualTo(200L);
        assertThat(result.storagePath().value()).isEqualTo("uploads/chat/100/attachments/200/");
        assertThat(result.fileType()).isEqualTo(FileType.DOCUMENT);

        ArgumentCaptor<FileAttachment> attachmentCaptor = ArgumentCaptor.forClass(FileAttachment.class);
        verify(saveFileAttachmentPort).save(attachmentCaptor.capture());
        FileAttachment captured = attachmentCaptor.getValue();
        assertThat(captured.messageId()).isEqualTo(200L);
        assertThat(captured.metadata().storagePath().value()).isEqualTo("uploads/chat/100/attachments/200/");
        assertThat(captured.metadata().storedFileName().value()).isEqualTo("stored-architecture.pdf");
    }

    @Test
    @DisplayName("삭제된 사용자는 첨부파일을 업로드할 수 없다")
    void upload_fail_whenRequesterDeleted() {
        UploadFileAttachmentCommand command = new UploadFileAttachmentCommand(
            1L,
            100L,
            200L,
            new ByteArrayInputStream("pdf".getBytes(StandardCharsets.UTF_8)),
            "architecture.pdf",
            "application/pdf",
            1024L
        );

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.deletedUser(1L)));

        assertThatThrownBy(() -> uploadFileAttachmentService.upload(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(UserErrorCode.USER_DELETED.getMessage());
    }

    @Test
    @DisplayName("채팅방 참여자가 아니면 첨부파일을 업로드할 수 없다")
    void upload_fail_whenParticipantNotFound() {
        UploadFileAttachmentCommand command = new UploadFileAttachmentCommand(
            1L,
            100L,
            200L,
            new ByteArrayInputStream("pdf".getBytes(StandardCharsets.UTF_8)),
            "architecture.pdf",
            "application/pdf",
            1024L
        );

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> uploadFileAttachmentService.upload(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_NOT_FOUND.getMessage());
    }
}
