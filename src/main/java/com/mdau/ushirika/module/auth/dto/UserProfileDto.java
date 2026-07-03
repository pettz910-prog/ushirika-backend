package com.mdau.ushirika.module.auth.dto;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.OfficialTitle;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.member.entity.MemberProfile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Full profile for the authenticated user — merges auth fields with the
 * optional MemberProfile row. Field names are intentionally aligned with
 * the frontend User interface so the fetch swap is zero-change on the client.
 */
public record UserProfileDto(
        UUID id,
        String memberId,
        String email,
        String firstName,
        String lastName,
        String fullName,
        String phone,
        /** "member" for regular members; "admin" for all staff roles. */
        String role,
        OfficialTitle officialTitle,
        boolean emailVerified,
        boolean active,
        /** "pending" | "active" | "suspended" | "ceased" */
        String status,
        boolean membershipCeased,
        /** "Standard" | "Family" | "Patron" */
        String tier,
        /** ISO date string (YYYY-MM-DD) — null until membership is approved. */
        LocalDate joinedAt,
        /** Nearest city / county of residence — maps to MemberProfile.county. */
        String city,
        String photoUrl
) {
    public static UserProfileDto from(User user, MemberProfile profile) {
        String memberId  = profile != null ? profile.getMemberId()       : null;
        String tier      = profile != null && profile.getMembershipTier() != null
                           ? profile.getMembershipTier() : "Standard";
        LocalDate joined = profile != null ? profile.getMemberSince()    : null;
        String city      = profile != null ? profile.getCounty()         : null;
        String photoUrl  = profile != null ? profile.getPhotoUrl()       : null;

        String status;
        if (user.isMembershipCeased()) {
            status = "ceased";
        } else if (!user.isActive()) {
            status = "suspended";
        } else if (user.getRole() != UserRole.MEMBER || memberId != null) {
            // non-member staff and fully-approved members are active
            status = "active";
        } else {
            status = "pending";
        }

        String role = user.getRole() == UserRole.MEMBER ? "member" : "admin";

        return new UserProfileDto(
                user.getId(),
                memberId,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhone(),
                role,
                user.getOfficialTitle(),
                user.isEmailVerified(),
                user.isActive(),
                status,
                user.isMembershipCeased(),
                tier,
                joined,
                city,
                photoUrl
        );
    }
}
