package com.lrchan.qootalk.infrastructure.persistence.audit;

import java.time.LocalDateTime;
import java.util.Objects;

import com.lrchan.qootalk.domain.audit.ActionType;
import com.lrchan.qootalk.domain.audit.ActorType;
import com.lrchan.qootalk.domain.audit.TargetType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AuditLogEntity {
    private Long id;
    private ActorType actorType;
    private Long actorId;
    private ActionType actionType;
    private TargetType targetType;
    private Long targetId;
    private String reason;
    private LocalDateTime occurredAt;
}
