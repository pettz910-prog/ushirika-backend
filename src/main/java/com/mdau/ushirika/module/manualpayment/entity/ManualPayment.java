package com.mdau.ushirika.module.manualpayment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentCategory;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "manual_payments",
    indexes = {
        @Index(name = "idx_mp_status",        columnList = "status"),
        @Index(name = "idx_mp_category",      columnList = "category"),
        @Index(name = "idx_mp_member_id",     columnList = "member_id"),
        @Index(name = "idx_mp_recorded_by",   columnList = "recorded_by_id"),
        @Index(name = "idx_mp_approved_by",   columnList = "approved_by_id"),
        @Index(name = "idx_mp_payment_date",  columnList = "payment_date"),
        @Index(name = "idx_mp_status_cat",    columnList = "status, category"),
        @Index(name = "idx_mp_created_at",    columnList = "created_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_mp_receipt_number", columnNames = "receipt_number")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualPayment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ManualPaymentCategory category;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    /**
     * Linked member — null if the payer is not a registered member (walk-in cash donor, etc.).
     * Required when category = CONTRIBUTION (enforced in service).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_mp_member"))
    private User member;

    /** Required when member is null so we still have an identity trail. */
    @Column(name = "payer_name", length = 200)
    private String payerName;

    @Column(name = "payer_email", length = 150)
    private String payerEmail;

    /** The date the cash was physically received — may differ from createdAt. */
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /**
     * Reference number on the physical receipt or voucher.
     * Unique — prevents the same receipt being entered twice.
     */
    @Column(name = "receipt_number", nullable = false, length = 100)
    private String receiptNumber;

    /**
     * For CONTRIBUTION type — which period this covers ("2025-06", "2025-Q2", "2025").
     * Null for non-contribution categories.
     */
    @Column(name = "period", length = 10)
    private String period;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private ManualPaymentStatus status = ManualPaymentStatus.PENDING;

    /** Who entered this record — snapshot identity stored in audit log. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mp_recorded_by"))
    private User recordedBy;

    /** Set on approval. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_mp_approved_by"))
    private User approvedBy;

    /** Set on rejection. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected_by_id", nullable = true,
                foreignKey = @ForeignKey(name = "fk_mp_rejected_by"))
    private User rejectedBy;

    /** Reason required on rejection. */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}
