package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.DeleteMessageCommand;
import com.lrchan.qootalk.application.chat.dto.result.DeleteMessageQueryResult;
import com.lrchan.qootalk.application.chat.port.in.DeleteMessageUsecase;
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
public class DeleteMessageService implements DeleteMessageUsecase {

    private final LoadUserPort loadUserPort;
    private final LoadMessagePort loadMessagePort;
    private final SaveMessagePort saveMessagePort;

    @Override
    public DeleteMessageQueryResult delete(DeleteMessageCommand command) {
        User user = loadUserPort.findById(command.requesterId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.deletedAt() != null) {
            throw new DomainException(UserErrorCode.USER_DELETED);
        }

        Message message = loadMessagePort.findById(command.messageId())
            .orElseThrow(() -> new DomainException(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND));

        if (!command.requesterId().equals(message.userId())) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_DELETE_FORBIDDEN);
        }
        if (message.messageType() == MessageType.SYSTEM || message.messageType() == MessageType.NOTICE) {
            throw new DomainException(ChatErrorCode.CHAT_MESSAGE_DELETE_NOT_ALLOWED);
        }

        message.delete();
        Message savedMessage = saveMessagePort.save(message);
        return new DeleteMessageQueryResult(savedMessage.isDeleted(), savedMessage.id());
    }
}
