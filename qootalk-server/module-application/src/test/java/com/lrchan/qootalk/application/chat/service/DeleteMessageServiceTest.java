package com.lrchan.qootalk.application.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lrchan.qootalk.application.chat.dto.command.DeleteMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

@ExtendWith(MockitoExtension.class)
class DeleteMessageServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadMessagePort loadMessagePort;
    @Mock
    private SaveMessagePort saveMessagePort;

    @InjectMocks
    private DeleteMessageService deleteMessageService;

    @Test
    @DisplayName("작성자는 메시지를 삭제할 수 있다")
    void delete_success() {
        DeleteMessageCommand command = new DeleteMessageCommand(1L, 100L);
        Message message = ChatServiceTestFixtures.message(100L, 10L, 1L, "삭제 대상", MessageType.TEXT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadMessagePort.findById(100L)).willReturn(Optional.of(message));
        given(saveMessagePort.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));

        DeleteMessageQueryResult result = deleteMessageService.delete(command);

        assertThat(result.deleted()).isTrue();
        assertThat(result.messageId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("작성자가 아닌 사용자는 메시지를 삭제할 수 없다")
    void delete_fail_whenRequesterIsNotAuthor() {
        DeleteMessageCommand command = new DeleteMessageCommand(2L, 100L);
        Message message = ChatServiceTestFixtures.message(100L, 10L, 1L, "삭제 대상", MessageType.TEXT);

        given(loadUserPort.findById(2L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(2L)));
        given(loadMessagePort.findById(100L)).willReturn(Optional.of(message));

        assertThatThrownBy(() -> deleteMessageService.delete(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_DELETE_FORBIDDEN.getMessage());
    }
}
