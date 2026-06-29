package com.mdau.ushirika.module.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiateContributionRequest(

        /** Optional — if provided, amount is taken from the plan. */
        UUID planId,

        /** Required if planId is null (custom amount). */
        @DecimalMin(value = "1.00", message = "Custom amount must be at least $1.00")
        BigDecimal customAmount,

        /**
         * Period label set by the member. Examples:
         * Monthly → "2025-06", Quarterly → "2025-Q2", Annual → "2025"
         */
        @NotBlank(message = "Period label is required (e.g. 2025-06, 2025-Q2, 2025)")
        String period,

        /** Stripe redirects here after successful payment. Append ?session_id={CHECKOUT_SESSION_ID} is handled server-side. */
        @NotBlank(message = "Success URL is required")
        String successUrl,

        /** Stripe redirects here if the member cancels on the checkout page. */
        @NotBlank(message = "Cancel URL is required")
        String cancelUrl
) {}
