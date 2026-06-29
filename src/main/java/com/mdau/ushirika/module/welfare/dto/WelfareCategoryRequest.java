package com.mdau.ushirika.module.welfare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record WelfareCategoryRequest(

        @NotBlank(message = "Category name is required")
        String name,

        String description,

        /** Optional cap — null means no limit enforced per request. */
        @DecimalMin(value = "1.00", message = "Max amount must be at least 1.00 if set")
        BigDecimal maxAmount,

        boolean active
) {}
