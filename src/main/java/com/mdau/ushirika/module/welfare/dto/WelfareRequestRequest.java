package com.mdau.ushirika.module.welfare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WelfareRequestRequest(

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotNull(message = "Amount requested is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
        BigDecimal amountRequested,

        @NotBlank(message = "Description of need is required")
        String description,

        /** Cloudinary URLs for supporting documents uploaded before submission. */
        List<String> documentUrls
) {}
