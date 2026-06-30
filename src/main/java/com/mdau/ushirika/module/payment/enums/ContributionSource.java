package com.mdau.ushirika.module.payment.enums;

public enum ContributionSource {
    /** Confirmed via Stripe checkout.session.completed webhook. */
    STRIPE,
    /** Recorded and approved by a Financial Admin for a cash/in-person payment. */
    MANUAL,
    /** Member self-reported Zelle/Venmo/CashApp payment verified by two-sided TX reference match. */
    PEER
}
