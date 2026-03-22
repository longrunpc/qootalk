package com.lrchan.qootalk.infrastructure.messaging.presence;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPresenceHeartbeatScheduler {

    private final LocalUserConnectionRegistry localUserConnectionRegistry;
    private final RedisUserPresenceTracker redisUserPresenceTracker;

    @Scheduled(fixedDelayString = "${messaging.redis.presence-heartbeat-millis:60000}")
    public void refreshPresence() {
        for (LocalUserConnectionRegistry.ConnectionRef connection : localUserConnectionRegistry.activeConnections()) {
            redisUserPresenceTracker.refreshConnection(connection.userId(), connection.connectionId());
            localUserConnectionRegistry.sendHeartbeat(connection.userId(), connection.connectionId());
        }
    }
}
