package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordContributionRequest(
        @NotNull UUID slotId,
        @NotNull @Min(1) @Max(12) Integer month,
        @NotNull @DecimalMin("1.00") BigDecimal amount,
        @NotBlank String paymentMethod,
        String paymentReference,
        String notes
) {}
