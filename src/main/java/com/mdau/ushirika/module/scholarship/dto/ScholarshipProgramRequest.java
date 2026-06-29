package com.mdau.ushirika.module.scholarship.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScholarshipProgramRequest(

        @NotBlank(message = "Program name is required")
        String name,

        String description,

        String eligibilityCriteria,

        @NotNull(message = "Amount per recipient is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        BigDecimal amountPerRecipient,

        /** Null means unlimited slots. */
        @Positive(message = "Total slots must be a positive number if specified")
        Integer totalSlots,

        @NotBlank(message = "Academic year is required (e.g. 2025/2026)")
        String academicYear,

        @Future(message = "Application deadline must be a future date")
        LocalDate applicationDeadline
) {}
