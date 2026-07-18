package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.enums.Gender;
import com.mdau.ushirika.module.member.enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

/** Full editable profile returned by GET /users/me/full-profile. */
public record FullMemberProfileDto(
        UUID   id,
        String memberId,
        String email,
        String firstName,
        String lastName,
        String phone,
        String role,
        String photoUrl,

        // identity
        String      idNumber,
        Gender      gender,
        LocalDate   dateOfBirth,

        // address
        String address,
        String county,

        // family
        MaritalStatus maritalStatus,
        String        spouseName,

        // next of kin
        String nextOfKinName,
        String nextOfKinPhone,
        String nextOfKinRelationship,

        // emergency contact
        String emergencyContactName,
        String emergencyContactPhone,

        // occupation
        String occupation,
        String employer,

        // membership (read-only)
        LocalDate memberSince,
        String    membershipTier
) {
    public static FullMemberProfileDto from(User user, MemberProfile p) {
        String role = switch (user.getRole()) {
            case SUPERADMIN -> "superadmin";
            case ADMIN      -> "admin";
            case LEADERSHIP -> "leadership";
            default         -> "member";
        };
        return new FullMemberProfileDto(
                user.getId(),
                p != null ? p.getMemberId()       : null,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                role,
                p != null ? p.getPhotoUrl()       : null,
                p != null ? p.getIdNumber()        : null,
                p != null ? p.getGender()          : null,
                p != null ? p.getDateOfBirth()     : null,
                p != null ? p.getAddress()         : null,
                p != null ? p.getCounty()          : null,
                p != null ? p.getMaritalStatus()   : null,
                p != null ? p.getSpouseName()      : null,
                p != null ? p.getNextOfKinName()   : null,
                p != null ? p.getNextOfKinPhone()  : null,
                p != null ? p.getNextOfKinRelationship() : null,
                p != null ? p.getEmergencyContactName()  : null,
                p != null ? p.getEmergencyContactPhone() : null,
                p != null ? p.getOccupation()      : null,
                p != null ? p.getEmployer()        : null,
                p != null ? p.getMemberSince()     : null,
                p != null ? p.getMembershipTier()  : null
        );
    }
}
