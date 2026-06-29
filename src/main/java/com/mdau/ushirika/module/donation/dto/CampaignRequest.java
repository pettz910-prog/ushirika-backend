package com.mdau.ushirika.module.donation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignRequest(

        @NotBlank(message = "Campaign title is required")
        String title,

        String description,

        /** Null means no fundraising target. */
        @Positive(message = "Goal amount must be positive if specified")
        BigDecimal goalAmount,

        LocalDate startDate,
        LocalDate endDate,

        String coverImageUrl,

        boolean isPublic
) {}
