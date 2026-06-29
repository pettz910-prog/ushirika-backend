package com.mdau.ushirika.module.manualpayment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Per-official capability grants, managed exclusively by FINANCIAL_ADMIN.
 * FINANCIAL_ADMIN never needs a row here — their capabilities are unconditional.
 */
@Entity
@Table(
    name = "financial_official_permissions",
    indexes = {
        @Index(name = "idx_fop_official_id",   columnList = "official_id"),
        @Index(name = "idx_fop_granted_by_id", columnList = "granted_by_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_fop_official", columnNames = "official_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialOfficialPermission extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "official_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_fop_official"))
    private User official;

    /** Allows the official to enter new PENDING manual payments. */
    @Column(name = "can_record_payments", nullable = false)
    @Builder.Default
    private boolean canRecordPayments = false;

    /**
     * Allows the official to approve/reject PENDING payments they did NOT record.
     * Maker-checker is always enforced regardless of this flag.
     */
    @Column(name = "can_approve_payments", nullable = false)
    @Builder.Default
    private boolean canApprovePayments = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fop_granted_by"))
    private User grantedBy;
}
