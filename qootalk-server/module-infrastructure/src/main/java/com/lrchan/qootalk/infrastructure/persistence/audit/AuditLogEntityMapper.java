package com.lrchan.qootalk.infrastructure.persistence.audit;

import com.lrchan.qootalk.domain.audit.AuditLog;

public class AuditLogEntityMapper {
    
    private AuditLogEntityMapper() {
    }

    public static AuditLogEntity toEntity(AuditLog auditLog) {
        return new AuditLogEntity(auditLog.id(), auditLog.actorType(), auditLog.actorId(), auditLog.actionType(), auditLog.targetType(), auditLog.targetId(), auditLog.reason(), auditLog.occurredAt());
    }

    public static AuditLog toDomain(AuditLogEntity auditLogEntity) {
        return AuditLog.reconstruct(auditLogEntity.id(), auditLogEntity.actorType(), auditLogEntity.actorId(), auditLogEntity.actionType(), auditLogEntity.targetType(), auditLogEntity.targetId(), auditLogEntity.reason(), auditLogEntity.occurredAt());
    }
}
