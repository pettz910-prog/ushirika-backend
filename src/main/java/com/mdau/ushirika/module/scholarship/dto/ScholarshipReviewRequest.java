package com.mdau.ushirika.module.scholarship.dto;

import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.validation.constraints.NotNull;

public record ScholarshipReviewRequest(

        @NotNull(message = "Decision is required (APPROVED or REJECTED)")
        ApprovalDecision decision,

        String comment
) {}
