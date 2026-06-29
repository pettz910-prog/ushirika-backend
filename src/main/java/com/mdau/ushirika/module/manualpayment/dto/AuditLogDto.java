package com.mdau.ushirika.module.manualpayment.dto;

import com.mdau.ushirika.module.manualpayment.entity.ManualPaymentAuditLog;
import com.mdau.ushirika.module.manualpayment.enums.AuditAction;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogDto(
    UUID id,
    AuditAction action,
    String actorName,
    String actorEmail,
    String actorRole,
    ManualPaymentStatus previousStatus,
    ManualPaymentStatus newStatus,
    String note,
    LocalDateTime createdAt
) {
    public static AuditLogDto from(ManualPaymentAuditLog log) {
        return new AuditLogDto(
            log.getId(),
            log.getAction(),
            log.getActorName(),
            log.getActorEmail(),
            log.getActorRole(),
            log.getPreviousStatus(),
            log.getNewStatus(),
            log.getNote(),
            log.getCreatedAt()
        );
    }
}
