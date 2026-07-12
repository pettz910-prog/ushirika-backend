package com.mdau.ushirika.module.dues.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyDuesInstallmentRequest(
        @NotBlank(message = "Admin transaction reference is required")
        String adminTxReference
) {}
