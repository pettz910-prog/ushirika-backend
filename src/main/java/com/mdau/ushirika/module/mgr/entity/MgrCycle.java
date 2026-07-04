package com.mdau.ushirika.module.mgr.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.mgr.enums.CycleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "mgr_cycles",
    indexes = {
        @Index(name = "idx_mgr_cycle_status", columnList = "status"),
        @Index(name = "idx_mgr_cycle_year",   columnList = "year")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgrCycle extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int year;

    /** First day of the first contribution month. */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Derived: startDate + 11 months end-of-month. Stored for display. */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** Fixed: 24 */
    @Column(name = "total_slots", nullable = false)
    @Builder.Default
    private int totalSlots = 24;

    /** $100 per member per month. */
    @Column(name = "monthly_contribution", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal monthlyContribution = new BigDecimal("100.00");

    /** 2 payouts per month. Each payout = totalSlots × monthly / payoutsPerMonth. */
    @Column(name = "payouts_per_month", nullable = false)
    @Builder.Default
    private int payoutsPerMonth = 2;

    /** $1,200 per slot payout (24 × 100 / 2). Stored explicitly for flexibility. */
    @Column(name = "payout_amount_per_slot", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal payoutAmountPerSlot = new BigDecimal("1200.00");

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private CycleStatus status = CycleStatus.DRAFT;

    @Column(name = "activated_at")
    private java.time.LocalDateTime activatedAt;

    @Column(name = "completed_at")
    private java.time.LocalDateTime completedAt;

    @OneToMany(mappedBy = "cycle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MgrSlot> slots = new ArrayList<>();
}
