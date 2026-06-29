package com.mdau.ushirika.module.manualpayment.entity;

import com.mdau.ushirika.module.manualpayment.enums.AuditAction;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit record for every state change on a ManualPayment.
 * Stores name + email as snapshots — not FK references — so the trail
 * survives user renames, deactivation, or deletion.
 */
@Entity
@Table(
    name = "manual_payment_audit_logs",
    indexes = {
        @Index(name = "idx_mpal_payment_id",   columnList = "manual_payment_id"),
        @Index(name = "idx_mpal_actor_email",  columnList = "actor_email"),
        @Index(name = "idx_mpal_action",       columnList = "action"),
        @Index(name = "idx_mpal_created_at",   columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPaymentAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_payment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mpal_payment"))
    private ManualPayment manualPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private AuditAction action;

    /** Human-readable identity — snapshot, not a live FK. */
    @Column(name = "actor_name", nullable = false, length = 200)
    private String actorName;

    @Column(name = "actor_email", nullable = false, length = 150)
    private String actorEmail;

    @Column(name = "actor_role", nullable = false, length = 30)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 10)
    private ManualPaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 10)
    private ManualPaymentStatus newStatus;

    /** Rejection reason or any context note recorded at the time of action. */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
