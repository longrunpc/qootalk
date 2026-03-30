package com.lrchan.qootalk.presentation.api.chat.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.lrchan.qootalk.infrastructure.messaging.presence.LocalUserConnectionRegistry;
import com.lrchan.qootalk.infrastructure.messaging.presence.RedisUserPresenceTracker;
import com.lrchan.qootalk.presentation.global.auth.AuthenticatedUserProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat-stream")
@RequiredArgsConstructor
@Tag(name = "Chat Stream", description = "실시간 채팅 스트림 API")
public class ChatStreamController {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final LocalUserConnectionRegistry localUserConnectionRegistry;
    private final RedisUserPresenceTracker redisUserPresenceTracker;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
        summary = "실시간 채팅 스트림 연결",
        description = "현재 로그인한 사용자의 실시간 채팅 메시지 스트림을 구독합니다."
    )
    public ResponseEntity<SseEmitter> subscribe() throws Exception {
        Long userId = authenticatedUserProvider.getCurrentUserId();
        LocalUserConnectionRegistry.Connection connection = localUserConnectionRegistry.register(userId);
        redisUserPresenceTracker.markConnected(userId, connection.connectionId());

        connection.emitter().onCompletion(() -> {
            localUserConnectionRegistry.remove(userId, connection.connectionId());
            redisUserPresenceTracker.disconnect(userId, connection.connectionId());
        });
        connection.emitter().onTimeout(() -> {
            localUserConnectionRegistry.remove(userId, connection.connectionId());
            redisUserPresenceTracker.disconnect(userId, connection.connectionId());
            connection.emitter().complete();
        });
        connection.emitter().onError(error -> {
            localUserConnectionRegistry.remove(userId, connection.connectionId());
            redisUserPresenceTracker.disconnect(userId, connection.connectionId());
        });

        connection.emitter().send(SseEmitter.event()
            .name("connected")
            .data("subscribed"));

        return ResponseEntity.ok(connection.emitter());
    }
}
