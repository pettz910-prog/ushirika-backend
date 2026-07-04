package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.benevolence.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "benevolence_enrollments",
    indexes = {
        @Index(name = "idx_ben_enroll_user",   columnList = "user_id"),
        @Index(name = "idx_ben_enroll_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenevolenceEnrollment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_ben_enroll_user"))
    private User user;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    /** Running total of installment payments received (max $600). */
    @Column(name = "total_paid", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalPaid = BigDecimal.ZERO;

    /** Set when totalPaid reaches $600. */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** completedAt + 6 months. Member may claim only after this date. */
    @Column(name = "probation_ends_at")
    private LocalDate probationEndsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.PAYING;

    /** True once beneficiaries have been submitted — no further edits by member. */
    @Column(name = "beneficiaries_locked", nullable = false)
    @Builder.Default
    private boolean beneficiariesLocked = false;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BenevolenceBeneficiary> beneficiaries = new ArrayList<>();

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EnrollmentPayment> payments = new ArrayList<>();
}
