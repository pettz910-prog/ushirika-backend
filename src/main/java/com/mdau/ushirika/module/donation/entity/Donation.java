package com.mdau.ushirika.module.donation.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.donation.enums.DonationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
    name = "donations",
    indexes = {
        @Index(name = "idx_dn_campaign_id",     columnList = "campaign_id"),
        @Index(name = "idx_dn_donor_id",        columnList = "donor_id"),
        @Index(name = "idx_dn_status",          columnList = "status"),
        // Webhook lookup — must be fast
        @Index(name = "idx_dn_stripe_session",  columnList = "stripe_session_id"),
        @Index(name = "idx_dn_donated_at",      columnList = "donated_at"),
        // Financial summary queries
        @Index(name = "idx_dn_campaign_status", columnList = "campaign_id, status"),
        @Index(name = "idx_dn_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation extends BaseEntity {

    /** Null means an uncampaigned (general) donation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id",
                foreignKey = @ForeignKey(name = "fk_dn_campaign"))
    private DonationCampaign campaign;

    /** Null for guest (public) donations. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id",
                foreignKey = @ForeignKey(name = "fk_dn_donor"))
    private User donor;

    /** Populated for guests; mirrors user.fullName for members (set at init). */
    @Column(name = "donor_name", length = 150)
    private String donorName;

    /** Populated for guests; mirrors user.email for members (set at init). */
    @Column(name = "donor_email", length = 150, nullable = false)
    private String donorEmail;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    /** Optional message from the donor. */
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    /** Stripe Checkout Session ID — set at session creation, used to match incoming webhook. */
    @Column(name = "stripe_session_id", nullable = false, unique = true, length = 120)
    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private DonationStatus status = DonationStatus.PENDING;

    /** Full Stripe webhook data stored for audit/reconciliation. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "webhook_data", columnDefinition = "jsonb")
    private Map<String, Object> webhookData;

    /** Set when checkout.session.completed webhook arrives. */
    @Column(name = "donated_at")
    private LocalDateTime donatedAt;

    /** Frontend URL Stripe redirects to after successful payment. */
    @Column(name = "success_url", length = 500)
    private String successUrl;
}
