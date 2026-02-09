package com.lrchan.qootalk.application.user.service;

import com.lrchan.qootalk.application.user.dto.command.DeleateProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.error.UserApplicationErrorCode;
import com.lrchan.qootalk.application.user.port.in.DeleteProfileImageUsecase;
import com.lrchan.qootalk.application.user.port.out.DeleteFilePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.common.exception.ApplicationException;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProfileImageService implements DeleteProfileImageUsecase {
    
    private final DeleteFilePort deleteProfileImagePort;
    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;

    @Override
    public UserQueryResult delete(DeleateProfileImageCommand command) {
        User user = loadUserPort.findById(command.userId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
        
        if (user.profileImageUrl().value() == null) {
            return UserQueryResult.of(user);
        }

        if (!user.profileImageUrl().value().equals(command.profileImageUrl().value())) {
            throw new ApplicationException(UserApplicationErrorCode.USER_PROFILE_IMAGE_URL_MISMATCH);
        }

        deleteProfileImagePort.delete(command.profileImageUrl().value());

        user.changeProfileImageUrl(null);

        User updatedUser = saveUserPort.save(user);

        return UserQueryResult.of(updatedUser);
    }
}
