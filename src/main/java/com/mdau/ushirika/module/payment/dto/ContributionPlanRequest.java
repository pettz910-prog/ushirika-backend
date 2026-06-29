package com.mdau.ushirika.module.payment.dto;

import com.mdau.ushirika.module.payment.enums.ContributionFrequency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContributionPlanRequest(

        @NotBlank(message = "Plan name is required")
        String name,

        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        BigDecimal amount,

        @NotNull(message = "Frequency is required")
        ContributionFrequency frequency,

        boolean active
) {}
