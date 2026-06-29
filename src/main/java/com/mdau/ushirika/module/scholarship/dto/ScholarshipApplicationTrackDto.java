package com.mdau.ushirika.module.scholarship.dto;

import com.mdau.ushirika.module.scholarship.entity.ScholarshipApplication;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Safe member-facing view — no admin names, no internal notes. */
public record ScholarshipApplicationTrackDto(
        UUID id,
        String referenceNumber,
        ScholarshipProgramDto program,
        ScholarshipApplicationStatus status,
        String beneficiaryName,
        String institutionName,
        String courseOfStudy,
        String academicYear,
        String rejectionReason,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime approvedAt,
        AwardSummary award
) {

    public record AwardSummary(
            BigDecimal amountAwarded,
            String currency,
            String method,
            LocalDateTime awardedAt
    ) {}

    public static ScholarshipApplicationTrackDto from(ScholarshipApplication a) {
        AwardSummary awardSummary = null;
        if (a.getAward() != null) {
            var aw = a.getAward();
            awardSummary = new AwardSummary(
                    aw.getAmountAwarded(), aw.getCurrency(),
                    aw.getMethod().name(), aw.getAwardedAt()
            );
        }
        return new ScholarshipApplicationTrackDto(
                a.getId(), a.getReferenceNumber(),
                ScholarshipProgramDto.from(a.getProgram()),
                a.getStatus(), a.getBeneficiaryName(),
                a.getInstitutionName(), a.getCourseOfStudy(), a.getAcademicYear(),
                a.getRejectionReason(),
                a.getSubmittedAt(), a.getReviewedAt(), a.getApprovedAt(),
                awardSummary
        );
    }
}
