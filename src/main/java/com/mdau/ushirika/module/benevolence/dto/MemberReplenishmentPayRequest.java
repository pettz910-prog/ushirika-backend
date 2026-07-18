package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberReplenishmentPayRequest(
        @NotBlank String paymentMethod,
        @NotBlank String memberTxReference,
        String notes
) {}
