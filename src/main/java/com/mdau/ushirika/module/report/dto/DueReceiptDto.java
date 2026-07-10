package com.mdau.ushirika.module.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DueReceiptDto(
        UUID receiptId,
        String receiptNumber,
        String memberName,
        String memberId,
        String email,
        int year,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDateTime paidAt,
        String paymentMethod,
        String paymentReference
) {}
