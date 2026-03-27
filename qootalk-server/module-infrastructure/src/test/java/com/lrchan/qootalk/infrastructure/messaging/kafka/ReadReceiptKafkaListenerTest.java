package com.lrchan.qootalk.infrastructure.messaging.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;

import com.lrchan.qootalk.application.chat.dto.event.ReadReceiptEvent;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.domain.chat.participant.RoomParticipant;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.infrastructure.messaging.presence.RedisUserPresenceTracker;
import com.lrchan.qootalk.infrastructure.messaging.redis.MessagePublisher;

@ExtendWith(MockitoExtension.class)
class ReadReceiptKafkaListenerTest {

    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private RedisUserPresenceTracker redisUserPresenceTracker;
    @Mock
    private MessagePublisher messagePublisher;
    @Mock
    private ChannelTopic readReceiptChannelTopic;

    @InjectMocks
    private ReadReceiptKafkaListener readReceiptKafkaListener;

    @Test
    @DisplayName("읽음 이벤트를 수신하면 현재 접속 중인 다른 참여자에게만 Redis 이벤트를 발행한다")
    void onMessage_publishOnlyConnectedParticipantsExceptReader() {
        ReadReceiptEvent event = new ReadReceiptEvent(10L, 1L, 100L, LocalDateTime.now());

        given(loadRoomParticipantPort.findActiveByRoomId(10L)).willReturn(List.of(
            participant(1L, 10L),
            participant(2L, 10L),
            participant(3L, 10L)
        ));
        given(redisUserPresenceTracker.isConnected(2L)).willReturn(true);
        given(redisUserPresenceTracker.isConnected(3L)).willReturn(false);

        readReceiptKafkaListener.onMessage(event);

        verify(messagePublisher, times(1)).publish(eq(readReceiptChannelTopic), any());
    }

    private RoomParticipant participant(Long userId, Long roomId) {
        return RoomParticipant.reconstruct(
            userId,
            userId,
            roomId,
            0L,
            RoomRole.MEMBER,
            true,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusMinutes(1),
            null
        );
    }
}
