package com.mdau.ushirika.module.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ReviewLoanRequest(
        @NotBlank String decision,   // APPROVED | REJECTED | UNDER_REVIEW
        BigDecimal approvedAmount,
        BigDecimal interestRate,     // e.g. 0.10 = 10% flat per annum
        @Size(max = 1000) String adminNotes,
        @Size(max = 1000) String rejectionReason
) {}
