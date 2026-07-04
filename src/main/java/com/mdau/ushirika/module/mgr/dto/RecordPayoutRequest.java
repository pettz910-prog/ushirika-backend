package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.NotBlank;

public record RecordPayoutRequest(
        String paymentReference,
        @NotBlank String paymentMethod,
        String adminNotes
) {}
