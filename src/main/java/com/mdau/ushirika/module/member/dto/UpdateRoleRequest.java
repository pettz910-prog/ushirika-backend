package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.auth.enums.OfficialTitle;
import com.mdau.ushirika.module.auth.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(

        @NotNull(message = "Role is required")
        UserRole role,

        /** Official organizational title — can be set independently of role.
         *  A MEMBER can hold a title (e.g. PATRON) without admin approval powers. */
        OfficialTitle officialTitle
) {}
