package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lrchan.qootalk.application.chat.dto.command.UpdateMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.UpdateMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.in.UpdateMessageUsecase;
import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.chat.error.ChatErrorCode;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMessageService implements UpdateMessageUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadMessagePort loadMessagePort;
    private final SaveMessagePort saveMessagePort;

    @Override
    public UpdateMessageQueryResult update(UpdateMessageCommand command) {
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        Message message = loadMessagePort.findById(command.messageId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND));

        if (!command.requesterId().equals(message.userId())) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_EDIT_FORBIDDEN);
        }
        if (message.messageType() == MessageType.SYSTEM || message.messageType() == MessageType.NOTICE) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_UPDATE_NOT_ALLOWED);
        }
        if (!StringUtils.hasText(command.content())) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_INVALID_CONTENT);
        }

        message.changeContent(command.content().trim());
        Message savedMessage = saveMessagePort.save(message);
        return UpdateMessageQueryResult.of(savedMessage);
    }
}
