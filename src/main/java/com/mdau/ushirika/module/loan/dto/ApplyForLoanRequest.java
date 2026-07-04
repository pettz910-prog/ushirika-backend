package com.mdau.ushirika.module.loan.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ApplyForLoanRequest(
        @NotNull @DecimalMin("100.00") BigDecimal requestedAmount,
        @NotNull @Min(1) @Max(24) Integer termMonths,
        @NotBlank @Size(max = 500) String purpose,
        @NotNull @Size(min = 1, max = 3) List<UUID> guarantorUserIds
) {}
