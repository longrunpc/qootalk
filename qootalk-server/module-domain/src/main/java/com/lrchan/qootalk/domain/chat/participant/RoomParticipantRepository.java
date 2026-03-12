package com.lrchan.qootalk.domain.chat.participant;

import java.util.Optional;

import com.lrchan.qootalk.common.response.PagedResponse;

public interface RoomParticipantRepository {
    Optional<RoomParticipant> findById(Long id);
    Optional<RoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId);
    RoomParticipant save(RoomParticipant roomParticipant);
    PagedResponse<RoomParticipant> findPageByUserId(Long userId, int page, int size);
}
