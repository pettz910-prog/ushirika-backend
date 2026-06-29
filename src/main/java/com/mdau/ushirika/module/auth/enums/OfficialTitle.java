package com.mdau.ushirika.module.auth.enums;

import lombok.Getter;

/**
 * Organizational position — separate from UserRole.
 * A member can hold a title without having admin/approval power.
 * A SUPERADMIN assigns both role and title.
 */
@Getter
public enum OfficialTitle {
    CHAIRPERSON("Chairperson"),
    VICE_CHAIRPERSON("Vice Chairperson"),
    SECRETARY_GENERAL("Secretary General"),
    ASSISTANT_SECRETARY("Assistant Secretary"),
    TREASURER("Treasurer"),
    ASSISTANT_TREASURER("Assistant Treasurer"),
    WELFARE_COORDINATOR("Welfare Coordinator"),
    PROGRAMS_DIRECTOR("Programs Director"),
    PATRON("Patron"),
    TRUSTEE("Trustee"),
    AUDITOR("Auditor");

    private final String displayName;

    OfficialTitle(String displayName) {
        this.displayName = displayName;
    }
}
