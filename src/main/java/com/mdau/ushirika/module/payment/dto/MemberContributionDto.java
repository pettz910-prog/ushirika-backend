package com.mdau.ushirika.module.payment.dto;

import com.mdau.ushirika.module.payment.entity.MemberContribution;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MemberContributionDto(
        UUID id,
        String planName,
        BigDecimal amount,
        String currency,
        String period,
        String notes,
        String stripeSessionId,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static MemberContributionDto from(MemberContribution c) {
        return new MemberContributionDto(
                c.getId(),
                c.getPlan() != null ? c.getPlan().getName() : "Custom",
                c.getAmount(),
                c.getCurrency() != null ? c.getCurrency() : "USD",
                c.getPeriod(),
                c.getNotes(),
                c.getPayment() != null ? c.getPayment().getSessionId() : null,
                c.getPayment() != null ? c.getPayment().getPaidAt() : null,
                c.getCreatedAt()
        );
    }
}
