package com.mdau.ushirika.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyFinePaymentRequest(
        @NotBlank(message = "Admin transaction reference is required")
        String adminTxReference
) {}
