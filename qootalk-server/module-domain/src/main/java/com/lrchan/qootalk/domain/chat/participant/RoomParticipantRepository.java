package com.lrchan.qootalk.domain.chat.participant;

import java.util.Optional;

public interface RoomParticipantRepository {
    Optional<RoomParticipant> findById(Long id);
    RoomParticipant save(RoomParticipant roomParticipant);
}
