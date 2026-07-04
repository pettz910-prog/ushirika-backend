package com.mdau.ushirika.module.benevolence.dto;

import com.mdau.ushirika.module.benevolence.entity.EnrollmentPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EnrollmentPaymentDto(
        UUID id,
        BigDecimal amount,
        String paymentMethod,
        String paymentReference,
        LocalDateTime paidAt,
        String notes
) {
    public static EnrollmentPaymentDto from(EnrollmentPayment p) {
        return new EnrollmentPaymentDto(
                p.getId(), p.getAmount(), p.getPaymentMethod(),
                p.getPaymentReference(), p.getPaidAt(), p.getNotes()
        );
    }
}
