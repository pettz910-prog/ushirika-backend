package com.mdau.ushirika.module.benevolence.dto;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceBeneficiary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BenevolenceBeneficiaryDto(
        UUID id,
        String firstName,
        String lastName,
        String relationship,
        String phoneNumber,
        LocalDate dateOfBirth,
        boolean deceased,
        LocalDateTime deceasedAt,
        String adminNotes
) {
    public static BenevolenceBeneficiaryDto from(BenevolenceBeneficiary b) {
        return new BenevolenceBeneficiaryDto(
                b.getId(), b.getFirstName(), b.getLastName(), b.getRelationship(),
                b.getPhoneNumber(), b.getDateOfBirth(), b.isDeceased(),
                b.getDeceasedAt(), b.getAdminNotes()
        );
    }
}
