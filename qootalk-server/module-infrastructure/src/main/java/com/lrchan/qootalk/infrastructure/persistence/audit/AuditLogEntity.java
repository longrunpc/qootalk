package com.lrchan.qootalk.infrastructure.persistence.audit;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.audit.ActionType;
import com.lrchan.qootalk.domain.audit.ActorType;
import com.lrchan.qootalk.domain.audit.TargetType;

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

    public AuditLogEntity(Long id, ActorType actorType, Long actorId, ActionType actionType, TargetType targetType, Long targetId, String reason, LocalDateTime occurredAt) {
        this.id = id;
        this.actorType = Objects.requireNonNull(actorType);
        this.actorId = Objects.requireNonNull(actorId);
        this.actionType = Objects.requireNonNull(actionType);
        this.targetType = Objects.requireNonNull(targetType);
        this.targetId = Objects.requireNonNull(targetId);
        this.reason = reason;
        this.occurredAt = Objects.requireNonNull(occurredAt);
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
