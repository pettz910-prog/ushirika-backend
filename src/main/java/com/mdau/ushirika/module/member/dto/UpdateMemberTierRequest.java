package com.mdau.ushirika.module.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMemberTierRequest(
        @NotBlank(message = "Tier name is required")
        @Size(max = 50, message = "Tier name must be 50 characters or fewer")
        String tier
) {}
