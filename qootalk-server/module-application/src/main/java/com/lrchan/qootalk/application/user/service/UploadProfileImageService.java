package com.lrchan.qootalk.application.user.service;

import org.springframework.stereotype.Service;

import com.lrchan.qootalk.application.user.dto.command.UploadProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.UploadProfileImageUsecase;
import com.lrchan.qootalk.application.user.port.out.DeleteFilePort;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;
import com.lrchan.qootalk.application.user.port.out.SaveUserPort;
import com.lrchan.qootalk.application.user.port.out.UploadProfileImagePort;
import com.lrchan.qootalk.common.exception.DomainException;
import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.domain.user.User;
import com.lrchan.qootalk.domain.user.error.UserErrorCode;
import com.lrchan.qootalk.domain.user.vo.ProfileImageUrl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadProfileImageService implements UploadProfileImageUsecase {

    private final UploadProfileImagePort uploadProfileImagePort;
    private final DeleteFilePort deleteProfileImagePort;
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    
    @Override
    public UserQueryResult upload(UploadProfileImageCommand command) {
        User user = loadUserPort.findById(command.userId())
                        .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));

        String path = "profile/" + command.userId();
        StorageResource resource = new StorageResource(
            path,
            command.originalFileName(),
            command.contentType(),
            command.fileSize());
        String uri = uploadProfileImagePort.upload(command.inputStream(), resource);
                        
        if (user.profileImageUrl().value() != null) {
            deleteProfileImagePort.delete(user.profileImageUrl().value());
        }

        user.changeProfileImageUrl(new ProfileImageUrl(uri));

        saveUserPort.save(user);

        return UserQueryResult.of(user);
    }
}
