package com.mdau.ushirika.module.donation.dto;

/** Returned after a Stripe Checkout Session is created for a donation. */
public record DonationInitResponse(
        String sessionId,
        String checkoutUrl
) {}
