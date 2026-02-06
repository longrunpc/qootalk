package com.lrchan.qootalk.application.user.service;

import org.springframework.stereotype.Service;

import com.lrchan.qootalk.application.user.dto.command.UploadProfileImageCommand;
import com.lrchan.qootalk.application.user.dto.result.UserQueryResult;
import com.lrchan.qootalk.application.user.port.in.UploadprofileImageUsecase;
import com.lrchan.qootalk.application.user.port.out.UploadProfileImagePort;
import com.lrchan.qootalk.common.storage.vo.StorageResource;
import com.lrchan.qootalk.domain.user.User;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UploadProfileImageService implements UploadprofileImageUsecase {

    private final UploadProfileImagePort uploadProfileImagePort;

    @Override
    public UserQueryResult upload(UploadProfileImageCommand command) {
        String path = "profile/" + command.userId();
        StorageResource resource = new StorageResource(
            "",
            command.originalFileName(),
            command.contentType(),
            command.fileSize());
        String uri = uploadProfileImagePort.upload(command.inputStream(), resource);

        // 유저 프로필 이미지 URL 업데이트 후 반환

        return null;
    }
    
}
