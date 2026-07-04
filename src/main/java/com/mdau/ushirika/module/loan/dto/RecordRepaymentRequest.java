package com.mdau.ushirika.module.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordRepaymentRequest(
        @NotNull UUID installmentId,
        @NotNull @DecimalMin("0.01") BigDecimal amountPaid,
        @NotBlank @Size(max = 100) String paymentMethod,
        @Size(max = 100) String paymentReference,
        @Size(max = 500) String notes
) {}
