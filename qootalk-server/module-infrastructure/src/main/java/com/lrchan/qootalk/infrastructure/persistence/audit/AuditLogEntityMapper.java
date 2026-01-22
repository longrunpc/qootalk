package com.lrchan.qootalk.infrastructure.persistence.audit;

import com.lrchan.qootalk.domain.audit.AuditLog;

public class AuditLogEntityMapper {
    
    private AuditLogEntityMapper() {
    }

    public static AuditLogEntity toEntity(AuditLog auditLog) {
        return AuditLogEntity.builder()
                .id(auditLog.id())
                .actorType(auditLog.actorType())
                .actorId(auditLog.actorId())
                .actionType(auditLog.actionType())
                .targetType(auditLog.targetType())
                .targetId(auditLog.targetId())
                .reason(auditLog.reason())
                .occurredAt(auditLog.occurredAt())
                .build();
    }

    public static AuditLog toDomain(AuditLogEntity auditLogEntity) {
        return AuditLog.reconstruct(auditLogEntity.id(), auditLogEntity.actorType(), auditLogEntity.actorId(), auditLogEntity.actionType(), auditLogEntity.targetType(), auditLogEntity.targetId(), auditLogEntity.reason(), auditLogEntity.occurredAt());
    }
}
