package com.lrchan.qootalk.domain.chat.message;

import java.util.Optional;

public interface MessageRepository {
    Optional<Message> findById(Long id);
    Optional<Message> findByParentMessageId(Long parentMessageId);
    Message save(Message message);
}
