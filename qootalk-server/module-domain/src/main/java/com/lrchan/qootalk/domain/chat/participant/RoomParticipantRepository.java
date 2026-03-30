package com.lrchan.qootalk.domain.chat.participant;

import java.util.List;
import java.util.Optional;

import com.lrchan.qootalk.common.response.PagedResponse;

public interface RoomParticipantRepository {
    Optional<RoomParticipant> findById(Long id);
    Optional<RoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId);
    RoomParticipant save(RoomParticipant roomParticipant);
    List<RoomParticipant> findActiveByRoomId(Long roomId);
    PagedResponse<RoomParticipant> findActivePageByUserId(Long userId, int page, int size);
}
