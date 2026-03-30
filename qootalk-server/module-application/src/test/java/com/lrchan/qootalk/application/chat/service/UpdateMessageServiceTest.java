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

import com.lrchan.qootalk.application.chat.dto.command.UpdateMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;

@ExtendWith(MockitoExtension.class)
class UpdateMessageServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadMessagePort loadMessagePort;
    @Mock
    private SaveMessagePort saveMessagePort;

    @InjectMocks
    private UpdateMessageService updateMessageService;

    @Test
    @DisplayName("작성자는 메시지 내용을 수정할 수 있다")
    void update_success() {
        UpdateMessageCommand command = new UpdateMessageCommand(1L, 100L, "수정된 메시지");
        Message message = ChatServiceTestFixtures.message(100L, 10L, 1L, "기존 메시지", MessageType.TEXT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadMessagePort.findById(100L)).willReturn(Optional.of(message));
        given(saveMessagePort.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));

        UpdateMessageQueryResult result = updateMessageService.update(command);

        assertThat(result.messageId()).isEqualTo(100L);
        assertThat(result.content()).isEqualTo("수정된 메시지");
    }

    @Test
    @DisplayName("작성자가 아닌 사용자는 메시지를 수정할 수 없다")
    void update_fail_whenRequesterIsNotAuthor() {
        UpdateMessageCommand command = new UpdateMessageCommand(2L, 100L, "수정 시도");
        Message message = ChatServiceTestFixtures.message(100L, 10L, 1L, "기존 메시지", MessageType.TEXT);

        given(loadUserPort.findById(2L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(2L)));
        given(loadMessagePort.findById(100L)).willReturn(Optional.of(message));

        assertThatThrownBy(() -> updateMessageService.update(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_EDIT_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("빈 내용으로는 메시지를 수정할 수 없다")
    void update_fail_whenContentBlank() {
        UpdateMessageCommand command = new UpdateMessageCommand(1L, 100L, "   ");
        Message message = ChatServiceTestFixtures.message(100L, 10L, 1L, "기존 메시지", MessageType.TEXT);

        given(loadUserPort.findById(1L)).willReturn(Optional.of(ChatServiceTestFixtures.activeUser(1L)));
        given(loadMessagePort.findById(100L)).willReturn(Optional.of(message));

        assertThatThrownBy(() -> updateMessageService.update(command))
            .isInstanceOf(DomainException.class)
            .hasMessage(ChatErrorCode.CHAT_MESSAGE_INVALID_CONTENT.getMessage());
    }
}
