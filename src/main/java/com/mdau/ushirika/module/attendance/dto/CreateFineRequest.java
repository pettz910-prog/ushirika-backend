package com.mdau.ushirika.module.attendance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateFineRequest(
        @NotNull(message = "userId is required") UUID userId,
        UUID meetingId,
        @NotBlank(message = "Reason is required") String reason,
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive") BigDecimal amount,
        @NotNull(message = "Due date is required") LocalDate dueDate
) {}
