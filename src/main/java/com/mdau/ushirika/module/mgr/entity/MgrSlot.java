package com.mdau.ushirika.module.mgr.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.mgr.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "mgr_slots",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_mgr_slot_cycle_slot",   columnNames = {"cycle_id", "slot_number"}),
        @UniqueConstraint(name = "uq_mgr_slot_cycle_user",   columnNames = {"cycle_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_mgr_slot_cycle",  columnList = "cycle_id"),
        @Index(name = "idx_mgr_slot_user",   columnList = "user_id"),
        @Index(name = "idx_mgr_slot_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgrSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_slot_cycle"))
    private MgrCycle cycle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_slot_user"))
    private User user;

    /** 1–24. Determines payout month: month = ceil(slotNumber / 2). */
    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    /** 1–12, derived from slotNumber. */
    @Column(name = "payout_month", nullable = false)
    private int payoutMonth;

    /** 1 or 2 — which payout in that month. */
    @Column(name = "payout_order", nullable = false)
    private int payoutOrder;

    /** Date on which payout is scheduled. */
    @Column(name = "scheduled_payout_date", nullable = false)
    private LocalDate scheduledPayoutDate;

    @Column(name = "payout_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private SlotStatus status = SlotStatus.SCHEDULED;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MgrContribution> contributions = new ArrayList<>();
}
