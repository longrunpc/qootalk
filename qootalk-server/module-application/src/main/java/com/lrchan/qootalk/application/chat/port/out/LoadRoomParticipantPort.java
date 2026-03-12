package com.lrchan.qootalk.application.chat.port.out;

import java.util.List;
import java.util.Optional;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

public interface LoadRoomParticipantPort {
    Optional<RoomParticipant> findById(Long id);
    List<RoomParticipant> findByRoomId(Long roomId);
}
