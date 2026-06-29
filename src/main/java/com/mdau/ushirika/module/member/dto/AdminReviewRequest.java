package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.validation.constraints.NotNull;

public record AdminReviewRequest(

        @NotNull(message = "Decision is required (APPROVED or REJECTED)")
        ApprovalDecision decision,

        /** Optional internal comment — only visible to SUPERADMIN. */
        String comment
) {}
