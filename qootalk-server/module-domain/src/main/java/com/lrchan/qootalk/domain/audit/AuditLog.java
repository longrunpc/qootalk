package com.lrchan.qootalk.domain.audit;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditLog {
    private final Long id;
    private final ActorType actorType;
    private final Long actorId;
    private final ActionType actionType;
    private final TargetType targetType;
    private final Long targetId;
    private final String reason;
    private final LocalDateTime occurredAt;

    private AuditLog(Long id, ActorType actorType, Long actorId, ActionType actionType, TargetType targetType, Long targetId, String reason, LocalDateTime occurredAt) {
        this.id = id;
        this.actorType = Objects.requireNonNull(actorType);
        this.actorId = Objects.requireNonNull(actorId);
        this.actionType = Objects.requireNonNull(actionType);
        this.targetType = Objects.requireNonNull(targetType);
        this.targetId = Objects.requireNonNull(targetId);
        this.reason = reason;
        this.occurredAt = occurredAt == null ? LocalDateTime.now() : occurredAt;
    }

    public static AuditLog reconstruct(Long id, ActorType actorType, Long actorId, ActionType actionType, TargetType targetType, Long targetId, String reason, LocalDateTime occurredAt) {
        return new AuditLog(id, actorType, actorId, actionType, targetType, targetId, reason, occurredAt);
    }

    public static AuditLog of(ActorType actorType, Long actorId, ActionType actionType, TargetType targetType, Long targetId, String detail) {
        return new AuditLog(null, actorType, actorId, actionType, targetType, targetId, detail, LocalDateTime.now());
    }

    public static AuditLog simple(ActorType actorType, Long actorId, ActionType actionType, TargetType targetType, Long targetId) {
        return of(actorType, actorId, actionType, targetType, targetId, null);
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
