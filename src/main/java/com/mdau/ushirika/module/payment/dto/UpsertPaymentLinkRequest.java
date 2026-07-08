package com.mdau.ushirika.module.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertPaymentLinkRequest(

        @NotBlank(message = "Handle is required (phone, username, cashtag, or URL)")
        @Size(max = 500)
        String handle,

        @Size(max = 100)
        String displayName,

        String instructions,

        @Size(max = 500)
        String deepLinkUrl,

        boolean active,

        int displayOrder
) {}
