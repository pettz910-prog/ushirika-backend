package com.mdau.ushirika.module.payment.dto;

import java.math.BigDecimal;

/** Returned to client after a Stripe Checkout Session is created. */
public record PaymentInitDto(
        String sessionId,
        String checkoutUrl,
        BigDecimal amount,
        String currency
) {}
