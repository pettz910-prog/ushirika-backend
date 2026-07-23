package com.mdau.ushirika.module.auth.enums;

/**
 * SUPERADMIN        — CEO-level. Manages admin roster, approval thresholds, all config.
 * ADMIN             — Approving official (Secretary General, Treasurer, etc.). All must approve for APPROVED state.
 * FINANCIAL_ADMIN   — Manages all manual payments. Can record, approve/reject, and delegate capabilities to officials.
 * FINANCIAL_OFFICIAL — Can record manual payments. Approval/rejection rights are individually delegated by FINANCIAL_ADMIN.
 * LEADERSHIP        — Read-only view of all admin data. Cannot mutate any records. Assigned to org leadership observers.
 * MEMBER            — Regular or official member. Can apply, pay, RSVP. May hold an OfficialTitle without approval power.
 * APPLICANT         — Accepted-in-principle applicant completing onboarding (account setup, extra info, bylaws,
 *                     registration fee). Restricted to the /onboarding/** endpoints until membership is approved,
 *                     at which point the same account's role flips to MEMBER.
 */
public enum UserRole {
    SUPERADMIN,
    ADMIN,
    FINANCIAL_ADMIN,
    FINANCIAL_OFFICIAL,
    LEADERSHIP,
    MEMBER,
    APPLICANT
}
