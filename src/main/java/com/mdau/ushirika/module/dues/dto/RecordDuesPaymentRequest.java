package com.mdau.ushirika.module.dues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecordDuesPaymentRequest(
        @NotBlank String userId,
        @NotNull  Integer year,
        String paymentMethod,
        String paymentReference,
        String notes
) {}
