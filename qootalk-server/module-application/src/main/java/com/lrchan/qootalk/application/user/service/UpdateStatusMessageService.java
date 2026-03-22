package com.lrchan.qootalk.application.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.user.dto.command.UpdateStatusMessageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.error.UserApplicationErrorCode;
import com.lrchan.qootalk.application.user.port.in.UpdateStatusMessageUsecase;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.StatusMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateStatusMessageService implements UpdateStatusMessageUsecase {
    
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    public UserQueryResult update(UpdateStatusMessageCommand command) {
        User user = loadUserPort.findById(command.userId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        if (user.isDeleted()) {
            throw new ApplicationException(UserApplicationErrorCode.USER_DELETED);
        }

        // 상태 메시지 수정
        user.changeStatusMessage(new StatusMessage(command.statusMessage()));

        // 유저 저장
        User updatedUser = saveUserPort.save(user);

        return UserQueryResult.of(updatedUser);
    }
}
