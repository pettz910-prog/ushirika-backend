package com.mdau.ushirika.module.donation.dto;

import com.mdau.ushirika.module.donation.entity.DonationCampaign;
import com.mdau.ushirika.module.donation.enums.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CampaignDto(
        UUID id,
        String title,
        String description,
        BigDecimal goalAmount,
        BigDecimal totalRaised,
        String currency,
        CampaignStatus status,
        LocalDate startDate,
        LocalDate endDate,
        String coverImageUrl,
        boolean isPublic
) {
    public static CampaignDto from(DonationCampaign c, BigDecimal totalRaised) {
        return new CampaignDto(
                c.getId(), c.getTitle(), c.getDescription(),
                c.getGoalAmount(), totalRaised != null ? totalRaised : BigDecimal.ZERO,
                c.getCurrency(), c.getStatus(),
                c.getStartDate(), c.getEndDate(),
                c.getCoverImageUrl(), c.isPublic()
        );
    }
}
