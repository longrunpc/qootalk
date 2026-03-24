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

import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;
import com.lrchan.qootalk.application.chat.port.out.LoadRoomParticipantPort;
import com.lrchan.qootalk.domain.chat.message.MessageType;
import com.lrchan.qootalk.domain.chat.participant.RoomRole;
import com.lrchan.qootalk.infrastructure.messaging.redis.MessagePublisher;
import com.lrchan.qootalk.infrastructure.messaging.presence.RedisUserPresenceTracker;

@ExtendWith(MockitoExtension.class)
class ChatMessageKafkaListenerTest {

    @Mock
    private LoadRoomParticipantPort loadRoomParticipantPort;
    @Mock
    private RedisUserPresenceTracker redisUserPresenceTracker;
    @Mock
    private MessagePublisher messagePublisher;
    @Mock
    private ChannelTopic chatMessageChannelTopic;

    @InjectMocks
    private ChatMessageKafkaListener chatMessageKafkaListener;

    @Test
    @DisplayName("Kafka 메시지를 수신하면 현재 접속 중인 참여자에게만 Redis 이벤트를 발행한다")
    void onMessage_publishOnlyConnectedParticipants() {
        ChatMessageEvent event = new ChatMessageEvent(
            100L,
            10L,
            1L,
            "안녕하세요",
            MessageType.TEXT,
            List.of(),
            null,
            List.of(),
            LocalDateTime.now()
        );

        given(loadRoomParticipantPort.findActiveByRoomId(10L)).willReturn(List.of(
            com.lrchan.qootalk.domain.chat.participant.RoomParticipant.reconstruct(1L, 1L, 10L, 99L, RoomRole.MEMBER, LocalDateTime.now(), LocalDateTime.now(), null),
            com.lrchan.qootalk.domain.chat.participant.RoomParticipant.reconstruct(2L, 2L, 10L, 99L, RoomRole.MEMBER, LocalDateTime.now(), LocalDateTime.now(), null),
            com.lrchan.qootalk.domain.chat.participant.RoomParticipant.reconstruct(3L, 3L, 10L, 99L, RoomRole.MEMBER, LocalDateTime.now(), LocalDateTime.now(), null)
        ));
        given(redisUserPresenceTracker.isConnected(1L)).willReturn(true);
        given(redisUserPresenceTracker.isConnected(2L)).willReturn(false);
        given(redisUserPresenceTracker.isConnected(3L)).willReturn(true);

        chatMessageKafkaListener.onMessage(event);

        verify(messagePublisher, times(2)).publish(eq(chatMessageChannelTopic), any());
    }
}
