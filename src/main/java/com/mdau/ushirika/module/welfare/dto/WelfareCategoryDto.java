package com.mdau.ushirika.module.welfare.dto;

import com.mdau.ushirika.module.welfare.entity.WelfareCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record WelfareCategoryDto(
        UUID id,
        String name,
        String description,
        BigDecimal maxAmount,
        String currency,
        boolean active
) {
    public static WelfareCategoryDto from(WelfareCategory c) {
        return new WelfareCategoryDto(
                c.getId(), c.getName(), c.getDescription(),
                c.getMaxAmount(), c.getCurrency(), c.isActive()
        );
    }
}
