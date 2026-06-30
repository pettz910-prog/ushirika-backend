package com.mdau.ushirika.module.payment.enums;

public enum PeerPaymentStatus {
    /** Member has submitted their TX reference; awaiting admin verification. */
    PENDING,
    /** Admin's TX reference matched the member's — contribution confirmed. */
    VERIFIED,
    /** Admin explicitly rejected — member notified to re-submit. */
    REJECTED
}
