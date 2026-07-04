package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.benevolence.enums.ReplenishmentPaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "replenishment_payments",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_replen_pay_replen_enrollment",
        columnNames = {"replenishment_id", "enrollment_id"}
    ),
    indexes = {
        @Index(name = "idx_replen_pay_replen",    columnList = "replenishment_id"),
        @Index(name = "idx_replen_pay_enrollment", columnList = "enrollment_id"),
        @Index(name = "idx_replen_pay_status",     columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplenishmentPayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "replenishment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_replen_pay_replenishment"))
    private BenevolenceReplenishment replenishment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_replen_pay_enrollment"))
    private BenevolenceEnrollment enrollment;

    @Column(name = "amount_due", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountDue;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReplenishmentPaymentStatus status = ReplenishmentPaymentStatus.PENDING;
}
