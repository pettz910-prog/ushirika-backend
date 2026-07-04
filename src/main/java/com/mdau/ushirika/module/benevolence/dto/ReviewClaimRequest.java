package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ReviewClaimRequest(
        @NotBlank String decision,
        BigDecimal amountApproved,
        String rejectionReason,
        String adminNotes
) {}
