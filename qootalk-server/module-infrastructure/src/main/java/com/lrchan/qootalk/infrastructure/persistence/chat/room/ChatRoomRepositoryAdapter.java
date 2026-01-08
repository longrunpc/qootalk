package com.lrchan.qootalk.infrastructure.persistence.chat.room;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.domain.chat.room.ChatRoom;
import com.lrchan.qootalk.domain.chat.room.ChatRoomRepository;

@Component
public class ChatRoomRepositoryAdapter implements ChatRoomRepository {
    
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    
    public ChatRoomRepositoryAdapter(ChatRoomJpaRepository chatRoomJpaRepository) {
        this.chatRoomJpaRepository = chatRoomJpaRepository;
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return chatRoomJpaRepository.findById(id).map(ChatRoomEntityMapper::toDomain);
    }

    @Override
    public Optional<ChatRoom> findByRoomName(String roomName) {
        return chatRoomJpaRepository.findByRoomName(roomName).map(ChatRoomEntityMapper::toDomain);
    }

    @Override
    public boolean existsByRoomName(String roomName) {
        return chatRoomJpaRepository.existsByRoomName(roomName);
    }

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        ChatRoomEntity chatRoomEntity = ChatRoomEntityMapper.toEntity(chatRoom);
        ChatRoomEntity savedEntity = chatRoomJpaRepository.save(chatRoomEntity);
        return ChatRoomEntityMapper.toDomain(savedEntity);
    }
}
