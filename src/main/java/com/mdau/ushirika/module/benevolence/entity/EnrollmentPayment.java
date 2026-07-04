package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "enrollment_payments",
    indexes = @Index(name = "idx_enroll_pay_enrollment", columnList = "enrollment_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentPayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_enroll_pay_enrollment"))
    private BenevolenceEnrollment enrollment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_reference", length = 255)
    private String paymentReference;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(length = 500)
    private String notes;
}
