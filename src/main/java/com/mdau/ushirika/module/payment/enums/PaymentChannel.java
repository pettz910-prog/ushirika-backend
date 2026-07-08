package com.mdau.ushirika.module.payment.enums;

/** All payment channels the organisation accepts, including automated (Stripe) and manual (peer) methods. */
public enum PaymentChannel {
    ZELLE,
    VENMO,
    CASHAPP,
    STRIPE
}
