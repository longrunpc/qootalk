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
import org.springframework.data.domain.PageImpl;

import com.lrchan.qootalk.application.chat.dto.command.LoadFileAttachmentsCommand;
import com.lrchan.qootalk.application.chat.dto.result.FileAttachmentQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadChatRoomPort;
import com.lrchan.qootalk.application.chat.port.out.LoadFileAttachmentPort;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.attachment.FileType;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;

@ExtendWith(MockitoExtension.class)
class LoadFileAttachmentsServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadChatRoomPort loadChatRoomPort;
    @Mock
    private LoadFileAttachmentPort loadFileAttachmentPort;
    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;

    @InjectMocks
    private LoadFileAttachmentsService loadFileAttachmentsService;

    @Test
    @DisplayName("첨부파일 목록 조회에 성공하면 페이징 결과를 반환한다")
    void load_success() {
        LoadFileAttachmentsCommand command = new LoadFileAttachmentsCommand(1L, 100L, 2L, FileType.DOCUMENT, 0, 10);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.participant(10L, 1L, 100L, 200L, RoomRole.MEMBER, true)));
        given(loadFileAttachmentPort.findPageByRoomIdAndUploaderIdAndFileType(100L, 2L, FileType.DOCUMENT, 0, 10))
            .willReturn(new PageImpl<>(List.of(
                ChatServiceTestFixtures.attachment(300L, 100L, 200L, 2L, "architecture.pdf", FileType.DOCUMENT),
                ChatServiceTestFixtures.attachment(301L, 100L, 201L, 2L, "erd.pdf", FileType.DOCUMENT)
            )));

        PagedResponse<FileAttachmentQueryResult> result = loadFileAttachmentsService.load(command);

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content())
            .extracting(FileAttachmentQueryResult::id)
            .containsExactly(300L, 301L);
    }

    @Test
    @DisplayName("삭제된 채팅방의 첨부파일은 조회할 수 없다")
    void load_fail_whenRoomDeleted() {
        LoadFileAttachmentsCommand command = new LoadFileAttachmentsCommand(1L, 100L, 2L, FileType.DOCUMENT, 0, 10);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.deletedRoom(100L, 1L)));

        assertThatThrownBy(() -> loadFileAttachmentsService.load(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_DELETED.getMessage());
    }

    @Test
    @DisplayName("삭제된 참여자는 첨부파일 목록을 조회할 수 없다")
    void load_fail_whenParticipantDeleted() {
        LoadFileAttachmentsCommand command = new LoadFileAttachmentsCommand(1L, 100L, 2L, FileType.DOCUMENT, 0, 10);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadChatRoomPort.findById(100L)).willReturn(Optional.of(ChatServiceTestFixtures.activeRoom(100L, 1L)));
        given(loadRoomParticipantPort.findByUserIdAndRoomId(1L, 100L))
            .willReturn(Optional.of(ChatServiceTestFixtures.deletedParticipant(10L, 1L, 100L, 200L, RoomRole.MEMBER)));

        assertThatThrownBy(() -> loadFileAttachmentsService.load(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_ROOM_PARTICIPANT_DELETED.getMessage());
    }
}
