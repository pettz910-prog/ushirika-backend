package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordReplenishmentPaymentRequest(
        @NotNull UUID enrollmentId,
        @NotNull BigDecimal amountPaid,
        @NotBlank String paymentMethod,
        String paymentReference
) {}
