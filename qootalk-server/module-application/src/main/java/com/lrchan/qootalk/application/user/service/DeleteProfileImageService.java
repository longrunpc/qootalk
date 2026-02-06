package com.lrchan.qootalk.application.user.service;

import com.lrchan.qootalk.application.user.port.in.DeleteProfileImageUsecase;
import com.lrchan.qootalk.application.user.port.out.DeleteProfileImagePort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProfileImageService implements DeleteProfileImageUsecase {
    
    private final DeleteProfileImagePort deleteProfileImagePort;

    @Override
    public void delete(String uri) {
        deleteProfileImagePort.delete(uri);
    }
}
