package com.lrchan.qootalk.domain.chat.room;

import java.util.Optional;

public interface ChatRoomRepository {
    Optional<ChatRoom> findById(Long id);
    void save(ChatRoom chatRoom);
}
