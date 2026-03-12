package com.lrchan.qootalk.domain.chat.participant;

import java.util.Optional;

public interface RoomParticipantRepository {
    Optional<RoomParticipant> findById(Long id);
    Optional<RoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId);
    RoomParticipant save(RoomParticipant roomParticipant);
}
