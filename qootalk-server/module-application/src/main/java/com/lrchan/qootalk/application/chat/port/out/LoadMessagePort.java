package com.lrchan.qootalk.application.chat.port.out;

import java.util.Optional;

import com.lrchan.qootalk.domain.chat.message.Message;

public interface LoadMessagePort {
    Optional<Message> findById(Long id);
    Long countByRoomIdAndIdAfter(Long roomId, Long id);
}
