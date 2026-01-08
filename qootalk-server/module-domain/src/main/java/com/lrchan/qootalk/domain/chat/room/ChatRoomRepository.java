package com.lrchan.qootalk.domain.chat.room;

import java.util.Optional;

public interface ChatRoomRepository {
    Optional<ChatRoom> findById(Long id);
    Optional<ChatRoom> findByRoomName(String roomName);
    boolean existsByRoomName(String roomName);
    ChatRoom save(ChatRoom chatRoom);
}
