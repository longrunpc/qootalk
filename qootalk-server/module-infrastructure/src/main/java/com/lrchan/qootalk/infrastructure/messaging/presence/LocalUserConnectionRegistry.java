package com.lrchan.qootalk.infrastructure.messaging.presence;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.lrchan.qootalk.application.chat.dto.event.ChatMessageEvent;

@Component
public class LocalUserConnectionRegistry {

    private final Map<Long, Map<String, SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

    public Connection register(Long userId) {
        String connectionId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(0L);

        emittersByUser
            .computeIfAbsent(userId, ignored -> new ConcurrentHashMap<>())
            .put(connectionId, emitter);

        return new Connection(userId, connectionId, emitter);
    }

    public void remove(Long userId, String connectionId) {
        Map<String, SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null) {
            return;
        }

        emitters.remove(connectionId);
        if (emitters.isEmpty()) {
            emittersByUser.remove(userId);
        }
    }

    public void dispatch(Long userId, ChatMessageEvent event) {
        Map<String, SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.forEach((connectionId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("chat-message")
                    .id(String.valueOf(event.messageId()))
                    .data(event));
            } catch (IOException ex) {
                remove(userId, connectionId);
                emitter.completeWithError(ex);
            }
        });
    }

    public void sendHeartbeat(Long userId, String connectionId) {
        Map<String, SseEmitter> emitters = emittersByUser.get(userId);
        if (emitters == null) {
            return;
        }

        SseEmitter emitter = emitters.get(connectionId);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                .name("heartbeat")
                .data("ping"));
        } catch (IOException ex) {
            remove(userId, connectionId);
            emitter.completeWithError(ex);
        }
    }

    public Set<ConnectionRef> activeConnections() {
        Set<ConnectionRef> refs = ConcurrentHashMap.newKeySet();
        emittersByUser.forEach((userId, emitters) ->
            emitters.keySet().forEach(connectionId -> refs.add(new ConnectionRef(userId, connectionId)))
        );
        return refs;
    }

    public record Connection(Long userId, String connectionId, SseEmitter emitter) {
    }

    public record ConnectionRef(Long userId, String connectionId) {
    }
}
