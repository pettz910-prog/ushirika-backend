package com.mdau.ushirika.module.election.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewCandidacyRequest(
        @NotBlank String decision,   // "APPROVE" or "REJECT"
        String rejectionReason
) {}
