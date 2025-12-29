package com.lrchan.qootalk.domain.audit;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
