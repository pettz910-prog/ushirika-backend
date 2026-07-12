package com.mdau.ushirika.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectFinePaymentRequest(
        @NotBlank(message = "Rejection reason is required")
        String reason
) {}
