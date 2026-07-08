package com.mdau.ushirika.module.benevolence.dto;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceClaimCategory;

import java.util.UUID;

public record ClaimCategoryDto(
        UUID id,
        String name,
        String description,
        String eventDateLabel,
        String eventPersonLabel,
        boolean requiresDocuments,
        boolean active,
        int sortOrder
) {
    public static ClaimCategoryDto from(BenevolenceClaimCategory c) {
        return new ClaimCategoryDto(
                c.getId(), c.getName(), c.getDescription(),
                c.getEventDateLabel(), c.getEventPersonLabel(),
                c.isRequiresDocuments(), c.isActive(), c.getSortOrder()
        );
    }
}
