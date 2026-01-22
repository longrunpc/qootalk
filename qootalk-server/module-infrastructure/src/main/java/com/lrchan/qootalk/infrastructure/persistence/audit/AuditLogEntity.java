package com.lrchan.qootalk.infrastructure.persistence.audit;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.audit.ActionType;
import com.lrchan.qootalk.domain.audit.ActorType;
import com.lrchan.qootalk.domain.audit.TargetType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLogEntity {
    private Long id;
    private ActorType actorType;
    private Long actorId;
    private ActionType actionType;
    private TargetType targetType;
    private Long targetId;
    private String reason;
    private LocalDateTime occurredAt;

    protected AuditLogEntity() {
    }

    public Long id() {
        return id;
    }

    public ActorType actorType() {
        return actorType;
    }

    public Long actorId() {
        return actorId;
    }

    public ActionType actionType() {
        return actionType;
    }

    public TargetType targetType() {
        return targetType;
    }

    public Long targetId() {
        return targetId;
    }

    public String reason() {
        return reason;
    }

    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
