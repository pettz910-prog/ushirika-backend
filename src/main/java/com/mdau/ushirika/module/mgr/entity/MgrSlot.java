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
        @UniqueConstraint(name = "uq_mgr_slot_cycle_user", columnNames = {"cycle_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_mgr_slot_cycle",        columnList = "cycle_id"),
        @Index(name = "idx_mgr_slot_user",         columnList = "user_id"),
        @Index(name = "idx_mgr_slot_status",       columnList = "status"),
        @Index(name = "idx_mgr_slot_payout_month", columnList = "payout_month")
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

    /** Sequential number within the cycle (assigned at join approval). */
    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    /**
     * Month number (1–12) in which this member was drawn as a beneficiary.
     * NULL until the monthly draw assigns it.
     */
    @Column(name = "payout_month")
    private Integer payoutMonth;

    /**
     * Which payout position within that month (e.g. 1st or 2nd beneficiary).
     * NULL until drawn.
     */
    @Column(name = "payout_order")
    private Integer payoutOrder;

    /** Actual calendar date the payout is due. NULL until drawn. */
    @Column(name = "scheduled_payout_date")
    private LocalDate scheduledPayoutDate;

    /** Payout amount as configured on the cycle at draw time. */
    @Column(name = "payout_amount", precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private SlotStatus status = SlotStatus.SCHEDULED;

    /** Timestamp when this slot was selected in a monthly draw. */
    @Column(name = "drawn_at")
    private LocalDateTime drawnAt;

    /** Timestamp when admin recorded the disbursement. */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    /** Whether the beneficiary has confirmed they received the payout. */
    @Column(name = "receipt_confirmed", nullable = false)
    @Builder.Default
    private boolean receiptConfirmed = false;

    @Column(name = "receipt_confirmed_at")
    private LocalDateTime receiptConfirmedAt;

    @Column(name = "receipt_notes", length = 500)
    private String receiptNotes;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MgrContribution> contributions = new ArrayList<>();
}
