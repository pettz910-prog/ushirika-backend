package com.mdau.ushirika.module.auth.enums;

/**
 * SUPERADMIN        — CEO-level. Manages admin roster, approval thresholds, all config.
 * ADMIN             — Approving official (Secretary General, Treasurer, etc.). All must approve for APPROVED state.
 * FINANCIAL_ADMIN   — Manages all manual payments. Can record, approve/reject, and delegate capabilities to officials.
 * FINANCIAL_OFFICIAL — Can record manual payments. Approval/rejection rights are individually delegated by FINANCIAL_ADMIN.
 * MEMBER            — Regular or official member. Can apply, pay, RSVP. May hold an OfficialTitle without approval power.
 */
public enum UserRole {
    SUPERADMIN,
    ADMIN,
    FINANCIAL_ADMIN,
    FINANCIAL_OFFICIAL,
    MEMBER
}
