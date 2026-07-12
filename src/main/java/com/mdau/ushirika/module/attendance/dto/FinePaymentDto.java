package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.entity.FinePayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FinePaymentDto(
        UUID id,
        UUID fineId,
        UUID memberId,
        String memberName,
        String email,
        String fineReason,
        BigDecimal fineAmount,
        BigDecimal submittedAmount,
        String paymentMode,
        String memberTxReference,
        String adminTxReference,
        String status,
        String rejectionReason,
        String verifiedByName,
        LocalDateTime verifiedAt,
        String notes,
        LocalDateTime createdAt
) {
    public static FinePaymentDto from(FinePayment p) {
        return new FinePaymentDto(
                p.getId(),
                p.getFine().getId(),
                p.getMember().getId(),
                p.getMember().getFullName(),
                p.getMember().getEmail(),
                p.getFine().getReason(),
                p.getFine().getAmount(),
                p.getAmount(),
                p.getPaymentMode().name(),
                p.getMemberTxReference(),
                p.getAdminTxReference(),
                p.getStatus().name(),
                p.getRejectionReason(),
                p.getVerifiedBy() != null ? p.getVerifiedBy().getFullName() : null,
                p.getVerifiedAt(),
                p.getNotes(),
                p.getCreatedAt()
        );
    }

    public static FinePaymentDto memberView(FinePayment p) {
        return new FinePaymentDto(
                p.getId(),
                p.getFine().getId(),
                p.getMember().getId(),
                p.getMember().getFullName(),
                p.getMember().getEmail(),
                p.getFine().getReason(),
                p.getFine().getAmount(),
                p.getAmount(),
                p.getPaymentMode().name(),
                p.getMemberTxReference(),
                null,
                p.getStatus().name(),
                p.getRejectionReason(),
                null,
                p.getVerifiedAt(),
                p.getNotes(),
                p.getCreatedAt()
        );
    }
}
