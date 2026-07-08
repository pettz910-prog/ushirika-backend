package com.mdau.ushirika.module.mgr.enums;

public enum SlotStatus {
    /** Member is in the pool but has not yet been selected in a monthly draw. */
    SCHEDULED,
    /** Selected in the monthly draw — payout is due to this member. */
    DRAWN,
    /** Admin has disbursed the payout. Member must confirm receipt. */
    PAID,
    CANCELLED
}
