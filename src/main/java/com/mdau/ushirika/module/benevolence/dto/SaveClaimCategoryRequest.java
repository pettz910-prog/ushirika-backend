package com.mdau.ushirika.module.benevolence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveClaimCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @Size(max = 100) String eventDateLabel,
        @Size(max = 100) String eventPersonLabel,
        Boolean requiresDocuments,
        Boolean active,
        Integer sortOrder
) {}
