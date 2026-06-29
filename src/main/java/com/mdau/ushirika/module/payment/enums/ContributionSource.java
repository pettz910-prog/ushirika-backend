package com.mdau.ushirika.module.payment.enums;

public enum ContributionSource {
    /** Confirmed via Stripe checkout.session.completed webhook. */
    STRIPE,
    /** Recorded and approved by a Financial Admin for a Zelle/Venmo/CashApp payment. */
    MANUAL
}
