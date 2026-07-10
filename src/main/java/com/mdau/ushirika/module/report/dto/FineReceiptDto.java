package com.mdau.ushirika.module.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FineReceiptDto(
        UUID receiptId,
        String receiptNumber,
        String memberName,
        String memberId,
        String email,
        String reason,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDateTime paidAt,
        String meetingTitle
) {}
