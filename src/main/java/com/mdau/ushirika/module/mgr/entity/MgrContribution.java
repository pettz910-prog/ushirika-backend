package com.mdau.ushirika.module.mgr.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.mgr.enums.ContributionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "mgr_contributions",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_mgr_contribution_slot_month",
        columnNames = {"slot_id", "contribution_month"}
    ),
    indexes = {
        @Index(name = "idx_mgr_contrib_slot",   columnList = "slot_id"),
        @Index(name = "idx_mgr_contrib_cycle",  columnList = "cycle_id"),
        @Index(name = "idx_mgr_contrib_status", columnList = "status"),
        @Index(name = "idx_mgr_contrib_month",  columnList = "contribution_month")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgrContribution extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_contrib_slot"))
    private MgrSlot slot;

    /** Denormalised for efficient querying by cycle without joining through slot. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_contrib_cycle"))
    private MgrCycle cycle;

    /** Cycle month number 1–12. */
    @Column(name = "contribution_month", nullable = false)
    private int contributionMonth;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private ContributionStatus status = ContributionStatus.PENDING;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(length = 500)
    private String notes;
}
