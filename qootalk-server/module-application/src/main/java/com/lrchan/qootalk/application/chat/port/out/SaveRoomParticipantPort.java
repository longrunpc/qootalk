package com.lrchan.qootalk.application.chat.port.out;

import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;

public interface SaveRoomParticipantPort {
    RoomParticipant save(RoomParticipant roomParticipant);
}
