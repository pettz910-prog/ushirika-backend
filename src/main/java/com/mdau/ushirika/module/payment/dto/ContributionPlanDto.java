package com.mdau.ushirika.module.payment.dto;

import com.mdau.ushirika.module.payment.entity.ContributionPlan;
import com.mdau.ushirika.module.payment.enums.ContributionFrequency;

import java.math.BigDecimal;
import java.util.UUID;

public record ContributionPlanDto(
        UUID id,
        String name,
        String description,
        BigDecimal amount,
        String currency,
        ContributionFrequency frequency,
        boolean active
) {
    public static ContributionPlanDto from(ContributionPlan p) {
        return new ContributionPlanDto(
                p.getId(), p.getName(), p.getDescription(),
                p.getAmount(), p.getCurrency(), p.getFrequency(), p.isActive()
        );
    }
}
