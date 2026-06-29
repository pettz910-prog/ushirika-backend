package com.mdau.ushirika.module.scholarship.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.welfare.enums.DisbursementMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Records the actual scholarship award after an application is APPROVED.
 * Amount awarded may differ from the program's standard amount (e.g. partial).
 */
@Entity
@Table(
    name = "scholarship_awards",
    indexes = {
        @Index(name = "idx_sch_award_awarded_by", columnList = "awarded_by_id"),
        @Index(name = "idx_sch_award_awarded_at", columnList = "awarded_at"),
        @Index(name = "idx_sch_award_method",     columnList = "method")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipAward extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_sch_award_application"))
    private ScholarshipApplication application;

    @Column(name = "amount_awarded", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountAwarded;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private DisbursementMethod method;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "awarded_by_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sch_award_by"))
    private User awardedBy;

    @Column(name = "awarded_at", nullable = false)
    private LocalDateTime awardedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
