package com.mdau.ushirika.module.payment.dto;

import com.mdau.ushirika.module.payment.entity.ContributionPlan;
import com.mdau.ushirika.module.payment.enums.ContributionFrequency;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ContributionPlanDto(
        UUID id,
        String name,
        String description,
        BigDecimal amount,
        String currency,
        ContributionFrequency frequency,
        List<String> features,
        String badge,
        int displayOrder,
        boolean active
) {
    public static ContributionPlanDto from(ContributionPlan p) {
        return new ContributionPlanDto(
                p.getId(), p.getName(), p.getDescription(),
                p.getAmount(), p.getCurrency(), p.getFrequency(),
                p.getFeatures(), p.getBadge(), p.getDisplayOrder(), p.isActive()
        );
    }
}
