package com.mdau.ushirika.module.loan.dto;

import com.mdau.ushirika.module.loan.entity.LoanInstallment;
import com.mdau.ushirika.module.loan.enums.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanInstallmentDto(
        UUID id,
        int installmentNumber,
        LocalDate dueDate,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal totalDue,
        BigDecimal amountPaid,
        BigDecimal balance,
        InstallmentStatus status,
        LocalDateTime paidAt,
        String paymentMethod,
        String paymentReference,
        String notes
) {
    public static LoanInstallmentDto from(LoanInstallment i) {
        return new LoanInstallmentDto(
                i.getId(),
                i.getInstallmentNumber(),
                i.getDueDate(),
                i.getPrincipal(),
                i.getInterest(),
                i.getTotalDue(),
                i.getAmountPaid(),
                i.getTotalDue().subtract(i.getAmountPaid()),
                i.getStatus(),
                i.getPaidAt(),
                i.getPaymentMethod(),
                i.getPaymentReference(),
                i.getNotes()
        );
    }
}
