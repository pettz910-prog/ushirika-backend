package com.mdau.ushirika.module.loan.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.loan.enums.GuarantorStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "loan_guarantors",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_loan_guarantor",
        columnNames = {"loan_id", "guarantor_user_id"}
    ),
    indexes = {
        @Index(name = "idx_loan_guarantor_user", columnList = "guarantor_user_id"),
        @Index(name = "idx_loan_guarantor_loan", columnList = "loan_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanGuarantor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_guarantor_loan"))
    private LoanApplication loan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guarantor_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_guarantor_user"))
    private User guarantorUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private GuarantorStatus status = GuarantorStatus.PENDING;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(length = 500)
    private String notes;
}
