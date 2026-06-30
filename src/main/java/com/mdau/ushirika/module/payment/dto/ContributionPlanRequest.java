package com.mdau.ushirika.module.payment.dto;

import com.mdau.ushirika.module.payment.enums.ContributionFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContributionPlanRequest(

        @NotBlank(message = "Plan name is required")
        String name,

        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        BigDecimal amount,

        @NotNull(message = "Frequency is required")
        ContributionFrequency frequency,

        /** Ordered list of privilege/feature strings displayed as bullet points. */
        List<String> features,

        /** Optional badge text shown on the plan card, e.g. "Most Common". Null = no badge. */
        String badge,

        /** Display order on the membership page — lower value appears first. */
        int displayOrder,

        boolean active
) {}
