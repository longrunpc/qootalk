package com.lrchan.qootalk.infrastructure.persistence.chat.message;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.application.chat.port.out.LoadMessagePort;
import com.lrchan.qootalk.application.chat.port.out.SaveMessagePort;
import com.lrchan.qootalk.domain.chat.message.Message;
import com.lrchan.qootalk.domain.chat.message.MessageRepository;

@Component
public class MessageRepositoryAdapter implements MessageRepository, SaveMessagePort, LoadMessagePort {
    private final MessageJpaRepository messageJpaRepository;

    public MessageRepositoryAdapter(MessageJpaRepository messageJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
    }
    
    @Override
    public Optional<Message> findById(Long id) {
        return messageJpaRepository.findById(Objects.requireNonNull(id)).map(MessageEntityMapper::toDomain);
    }

    @Override
    public Message save(Message message) {
        MessageEntity messageEntity = MessageEntityMapper.toEntity(message);
        MessageEntity savedEntity = messageJpaRepository.save(Objects.requireNonNull(messageEntity));
        return MessageEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Long countByRoomIdAndIdAfter(Long roomId, Long id) {
        return messageJpaRepository.countByRoomIdAndIdAfter(roomId, id);
    }
}
