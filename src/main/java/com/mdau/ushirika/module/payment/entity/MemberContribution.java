package com.mdau.ushirika.module.payment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.payment.enums.ContributionSource;
import com.mdau.ushirika.module.payment.entity.PeerPayment;
import com.mdau.ushirika.module.payment.entity.StripePayment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * A confirmed member contribution — created ONLY after a successful
 * Stripe webhook. Never created at payment initialization.
 */
@Entity
@Table(
    name = "member_contributions",
    indexes = {
        // Member self-service history
        @Index(name = "idx_mc_user_id",      columnList = "user_id"),
        // Financial reports: contributions per plan
        @Index(name = "idx_mc_plan_id",      columnList = "plan_id"),
        // "Has this member already paid for this period?"
        @Index(name = "idx_mc_user_period",  columnList = "user_id, period"),
        // Date-range financial reports
        @Index(name = "idx_mc_created_at",   columnList = "created_at"),
        // Idempotency check for peer-payment-linked contributions
        @Index(name = "idx_mc_peer_payment", columnList = "peer_payment_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberContribution extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mc_user"))
    private User member;

    /** Nullable — member may pay a custom amount without a plan. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id",
                foreignKey = @ForeignKey(name = "fk_mc_plan"))
    private ContributionPlan plan;

    /**
     * Set for STRIPE contributions. Null for MANUAL contributions.
     * unique = true so one Stripe session cannot fund two contributions.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true, unique = true,
                foreignKey = @ForeignKey(name = "fk_mc_payment"))
    private StripePayment payment;

    /**
     * Set for MANUAL contributions. Null for STRIPE contributions.
     * Exactly one of payment or manualPayment must be non-null — enforced in service.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_payment_id", nullable = true, unique = true,
                foreignKey = @ForeignKey(name = "fk_mc_manual_payment"))
    private com.mdau.ushirika.module.manualpayment.entity.ManualPayment manualPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 10)
    @Builder.Default
    private ContributionSource source = ContributionSource.STRIPE;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    /**
     * Human-readable period label: "2025-06" (monthly), "2025-Q2" (quarterly),
     * "2025" (annual). Set by member at payment initiation.
     */
    @Column(name = "period", length = 10)
    private String period;

    /**
     * Set for PEER contributions (member-reported Zelle/Venmo/CashApp).
     * Null for STRIPE and MANUAL contributions.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_payment_id", nullable = true, unique = true,
                foreignKey = @ForeignKey(name = "fk_mc_peer_payment"))
    private PeerPayment peerPayment;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
