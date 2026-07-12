package com.mdau.ushirika.module.attendance.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.attendance.enums.FinePaymentStatus;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.payment.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A payment submission by a member for a specific fine.
 * Two-sided verification: member submits their TX reference; admin independently
 * enters the reference they received. When both match the fine is marked PAID.
 * A REJECTED submission may be followed by a new one (member can re-submit).
 */
@Entity
@Table(
    name = "fine_payments",
    indexes = {
        @Index(name = "idx_fp_fine_id",     columnList = "fine_id"),
        @Index(name = "idx_fp_member_id",   columnList = "member_id"),
        @Index(name = "idx_fp_status",      columnList = "status"),
        @Index(name = "idx_fp_fine_status", columnList = "fine_id, status"),
        @Index(name = "idx_fp_member_tx",   columnList = "member_tx_reference"),
        @Index(name = "idx_fp_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinePayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fine_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fp_fine"))
    private Fine fine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fp_member"))
    private User member;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 10)
    private PaymentMode paymentMode;

    /** TX reference entered by the member immediately after payment. */
    @Column(name = "member_tx_reference", nullable = false, length = 100)
    private String memberTxReference;

    /** TX reference entered by the admin from their own app. Set only on verify. */
    @Column(name = "admin_tx_reference", length = 100)
    private String adminTxReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private FinePaymentStatus status = FinePaymentStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id",
                foreignKey = @ForeignKey(name = "fk_fp_verified_by"))
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
