package com.mdau.ushirika.module.auth.dto;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.OfficialTitle;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.member.entity.MemberProfile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Full profile for the authenticated user — merges auth fields with the
 * optional MemberProfile row that is created when the member submits their
 * application and becomes non-null once the application is approved.
 */
public record UserProfileDto(
        UUID id,
        String memberId,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        UserRole role,
        OfficialTitle officialTitle,
        boolean emailVerified,
        boolean active,
        /** "pending" = no approved memberId yet | "active" = full member | "suspended" = deactivated */
        String status,
        String membershipTier,
        LocalDate memberSince,
        String county,
        String photoUrl
) {
    public static UserProfileDto from(User user, MemberProfile profile) {
        String memberId  = profile != null ? profile.getMemberId()       : null;
        String tier      = profile != null && profile.getMembershipTier() != null
                           ? profile.getMembershipTier() : "Standard";
        LocalDate since  = profile != null ? profile.getMemberSince()    : null;
        String county    = profile != null ? profile.getCounty()         : null;
        String photoUrl  = profile != null ? profile.getPhotoUrl()       : null;

        String status;
        if (!user.isActive()) {
            status = "suspended";
        } else if (memberId != null) {
            status = "active";
        } else {
            status = "pending";
        }

        return new UserProfileDto(
                user.getId(),
                memberId,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getOfficialTitle(),
                user.isEmailVerified(),
                user.isActive(),
                status,
                tier,
                since,
                county,
                photoUrl
        );
    }
}
