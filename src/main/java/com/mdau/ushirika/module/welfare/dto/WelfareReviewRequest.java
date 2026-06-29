package com.mdau.ushirika.module.welfare.dto;

import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.validation.constraints.NotNull;

public record WelfareReviewRequest(

        @NotNull(message = "Decision is required (APPROVED or REJECTED)")
        ApprovalDecision decision,

        /** Optional — visible only to SUPERADMIN. */
        String comment
) {}
