package com.mdau.ushirika.module.dues.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.dues.enums.DuesPaymentStatus;
import com.mdau.ushirika.module.payment.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One installment payment toward a member's annual MembershipDue.
 * Two-sided verification: member enters their TX reference; admin independently
 * enters the reference they received. Both must match (case-insensitive) for
 * the installment to be VERIFIED and applied to the running paidAmount.
 */
@Entity
@Table(
    name = "dues_payments",
    indexes = {
        @Index(name = "idx_dp_dues_id",     columnList = "dues_id"),
        @Index(name = "idx_dp_member_id",   columnList = "member_id"),
        @Index(name = "idx_dp_status",      columnList = "status"),
        @Index(name = "idx_dp_dues_status", columnList = "dues_id, status"),
        @Index(name = "idx_dp_member_tx",   columnList = "member_tx_reference"),
        @Index(name = "idx_dp_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuesPayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dues_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_dp_dues"))
    private MembershipDue dues;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_dp_member"))
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
    private DuesPaymentStatus status = DuesPaymentStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id",
                foreignKey = @ForeignKey(name = "fk_dp_verified_by"))
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
