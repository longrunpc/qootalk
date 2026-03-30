package com.lrchan.qootalk.application.chat.port.out;

import java.util.Optional;

import org.springframework.data.domain.Slice;

import com.lrchan.qootalk.domain.chat.message.Message;

public interface LoadMessagePort {
    Optional<Message> findById(Long id);
    Long countByRoomIdAndIdAfter(Long roomId, Long id);
    Slice<Message> findSliceByRoomId(Long roomId, Long fromMessageId, int page, int size);
}
