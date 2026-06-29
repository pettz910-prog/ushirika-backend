package com.mdau.ushirika.module.payment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.payment.enums.PaymentPurpose;
import com.mdau.ushirika.module.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * One row per Stripe Checkout Session, regardless of purpose.
 * Created on session creation; status updated by the webhook.
 * Business records (MemberContribution, Donation) are ONLY created after
 * a checkout.session.completed webhook — never on session creation.
 */
@Entity
@Table(
    name = "stripe_payments",
    indexes = {
        @Index(name = "idx_sp_session_id",      columnList = "session_id"),
        @Index(name = "idx_sp_user_id",         columnList = "user_id"),
        @Index(name = "idx_sp_status",          columnList = "status"),
        @Index(name = "idx_sp_purpose",         columnList = "purpose"),
        @Index(name = "idx_sp_status_purpose",  columnList = "status, purpose"),
        @Index(name = "idx_sp_paid_at",         columnList = "paid_at"),
        @Index(name = "idx_sp_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePayment extends BaseEntity {

    /** Stripe Checkout Session ID (cs_...). Used for webhook lookup. */
    @Column(name = "session_id", unique = true, nullable = false, updatable = false, length = 120)
    private String sessionId;

    /** Stripe PaymentIntent ID (pi_...). Populated after session completes. */
    @Column(name = "payment_intent_id", length = 120)
    private String paymentIntentId;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** Amount in USD. */
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 20)
    private PaymentPurpose purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_sp_user"))
    private User user;

    /** UUID string of the plan or campaign this payment is for. */
    @Column(name = "purpose_entity_id", length = 36)
    private String purposeEntityId;

    /** Full webhook payload stored for audit and replay. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "webhook_data", columnDefinition = "jsonb")
    private Map<String, Object> webhookData;

    /** Set when checkout.session.completed webhook arrives. */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
