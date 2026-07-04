package com.mdau.ushirika.module.benevolence.dto;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceClaim;
import com.mdau.ushirika.module.benevolence.enums.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BenevolenceClaimDto(
        UUID id,
        UUID enrollmentId,
        String memberName,
        String memberId,
        UUID beneficiaryId,
        String beneficiaryName,
        String referenceNumber,
        String deceasedName,
        String relationship,
        LocalDate dateOfDeath,
        String locationOfDeath,
        LocalDate funeralDate,
        String funeralLocation,
        String contactName,
        String contactPhone,
        String description,
        List<String> documentUrls,
        BigDecimal amountApproved,
        ClaimStatus status,
        String rejectionReason,
        String adminNotes,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime disbursedAt
) {
    public static BenevolenceClaimDto from(BenevolenceClaim c, String memberId) {
        String fullName = c.getEnrollment().getUser().getFirstName()
                + " " + c.getEnrollment().getUser().getLastName();
        UUID beneficiaryId = c.getBeneficiary() != null ? c.getBeneficiary().getId() : null;
        String beneficiaryName = c.getBeneficiary() != null
                ? c.getBeneficiary().getFirstName() + " " + c.getBeneficiary().getLastName() : null;

        return new BenevolenceClaimDto(
                c.getId(), c.getEnrollment().getId(), fullName, memberId,
                beneficiaryId, beneficiaryName, c.getReferenceNumber(),
                c.getDeceasedName(), c.getRelationship(), c.getDateOfDeath(),
                c.getLocationOfDeath(), c.getFuneralDate(), c.getFuneralLocation(),
                c.getContactName(), c.getContactPhone(), c.getDescription(),
                c.getDocumentUrls(), c.getAmountApproved(), c.getStatus(),
                c.getRejectionReason(), c.getAdminNotes(),
                c.getSubmittedAt(), c.getReviewedAt(), c.getDisbursedAt()
        );
    }
}
