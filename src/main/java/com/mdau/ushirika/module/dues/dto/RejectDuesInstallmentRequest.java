package com.mdau.ushirika.module.dues.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectDuesInstallmentRequest(
        @NotBlank(message = "Rejection reason is required")
        String reason
) {}
