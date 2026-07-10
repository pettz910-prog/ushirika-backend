package com.mdau.ushirika.module.audit.service;

import com.mdau.ushirika.module.audit.entity.AuditLog;
import com.mdau.ushirika.module.audit.repository.AuditLogRepository;
import com.mdau.ushirika.module.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an admin action asynchronously so it never blocks the main transaction.
     *
     * @param actor      the authenticated User performing the action
     * @param action     short constant like "REINSTATEMENT_APPROVED", "FINE_WAIVED"
     * @param entityType the domain type e.g. "ReinstatementRequest", "Fine", "MembershipDue"
     * @param entityId   the UUID of the affected record (may be null for bulk actions)
     * @param description human-readable sentence describing what happened
     */
    @Async
    public void log(User actor, String action, String entityType, UUID entityId, String description) {
        try {
            AuditLog entry = AuditLog.builder()
                    .actorId(actor.getId())
                    .actorName(actor.getFullName())
                    .actorRole(actor.getRole().name())
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(description)
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.warn("AuditLogService: failed to persist audit entry [{}] for actor {}: {}",
                    action, actor.getId(), e.getMessage());
        }
    }
}
