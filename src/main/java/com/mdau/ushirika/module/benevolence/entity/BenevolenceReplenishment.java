package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.benevolence.enums.ReplenishmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "benevolence_replenishments",
    indexes = @Index(name = "idx_ben_replen_claim", columnList = "claim_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenevolenceReplenishment extends BaseEntity {

    /** The claim that triggered this replenishment (nullable for manual replenishments). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id",
                foreignKey = @ForeignKey(name = "fk_replen_claim"))
    private BenevolenceClaim claim;

    /** Total amount to be replenished across all active members. */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /** Amount owed per member (totalAmount / activeEnrolledCount at time of creation). */
    @Column(name = "per_member_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal perMemberAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReplenishmentStatus status = ReplenishmentStatus.ACTIVE;

    @OneToMany(mappedBy = "replenishment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReplenishmentPayment> memberPayments = new ArrayList<>();
}
