package com.lrchan.qootalk.application.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lrchan.qootalk.application.chat.dto.command.LoadChatRoomDetailCommand;
import com.lrchan.qootalk.application.chat.dto.result.ChatRoomDetailQueryResult;
import com.lrchan.qootalk.application.chat.port.in.LoadChatRoomDetailUsecase;
import com.lrchan.qootalk.application.user.port.out.LoadUserPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoadChatRoomDetailService implements LoadChatRoomDetailUsecase {
    
    private final LoadUserPort loadUserPort;
    
    @Override
    public ChatRoomDetailQueryResult load(LoadChatRoomDetailCommand command) {
        // 유저 검증
        loadUserPort.findById(command.userId())
            .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));

        return null;
    }
}
