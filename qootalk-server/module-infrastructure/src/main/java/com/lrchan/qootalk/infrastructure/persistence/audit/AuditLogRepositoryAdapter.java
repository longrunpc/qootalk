package com.lrchan.qootalk.infrastructure.persistence.audit;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.lrchan.qootalk.domain.audit.AuditLog;
import com.lrchan.qootalk.domain.audit.AuditLogRepository;

@Component
public class AuditLogRepositoryAdapter implements AuditLogRepository {
    private final AuditLogJpaRepository auditLogJpaRepository;
    
    public AuditLogRepositoryAdapter(AuditLogJpaRepository auditLogJpaRepository) {
        this.auditLogJpaRepository = auditLogJpaRepository;
    }

    @Override
    public void save(AuditLog auditLog) {
        AuditLogEntity auditLogEntity = AuditLogEntityMapper.toEntity(auditLog);
        auditLogJpaRepository.save(Objects.requireNonNull(auditLogEntity));
    }
}
