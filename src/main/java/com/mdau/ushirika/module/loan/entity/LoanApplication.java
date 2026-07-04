package com.mdau.ushirika.module.loan.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.loan.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "loan_applications",
    indexes = {
        @Index(name = "idx_loan_user",   columnList = "user_id"),
        @Index(name = "idx_loan_status", columnList = "status"),
        @Index(name = "idx_loan_ref",    columnList = "reference_number")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_loan_user"))
    private User user;

    @Column(name = "reference_number", nullable = false, unique = true, length = 30)
    private String referenceNumber;

    @Column(name = "requested_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false, length = 500)
    private String purpose;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    /** Set when admin approves. */
    @Column(name = "approved_amount", precision = 12, scale = 2)
    private BigDecimal approvedAmount;

    /** Flat annual rate stored as decimal, e.g. 0.10 = 10%. Set at approval. */
    @Column(name = "interest_rate", precision = 5, scale = 4)
    private BigDecimal interestRate;

    /** approvedAmount * (1 + interestRate). Set at disbursement. */
    @Column(name = "total_repayable", precision = 12, scale = 2)
    private BigDecimal totalRepayable;

    @Column(name = "total_paid", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "disbursed_at")
    private LocalDate disbursedAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "disbursement_method", length = 100)
    private String disbursementMethod;

    @Column(name = "disbursement_reference", length = 100)
    private String disbursementReference;

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "defaulted_at")
    private LocalDateTime defaultedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LoanGuarantor> guarantors = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("installmentNumber ASC")
    @Builder.Default
    private List<LoanInstallment> installments = new ArrayList<>();
}
