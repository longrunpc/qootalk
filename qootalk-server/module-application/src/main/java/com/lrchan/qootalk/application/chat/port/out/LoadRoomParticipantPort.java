package com.lrchan.qootalk.application.chat.port.out;

import java.util.List;
import java.util.Optional;

import com.lrchan.qootalk.common.response.PagedResponse;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

public interface LoadRoomParticipantPort {
    Optional<RoomParticipant> findById(Long id);
    Optional<RoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId);
    List<RoomParticipant> findByRoomId(Long roomId);
    List<RoomParticipant> findActiveByRoomId(Long roomId);
    PagedResponse<RoomParticipant> findActivePageByUserId(Long userId, int page, int size);
}
