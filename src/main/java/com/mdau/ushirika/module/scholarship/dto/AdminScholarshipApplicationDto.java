package com.mdau.ushirika.module.scholarship.dto;

import com.mdau.ushirika.module.auth.dto.UserDto;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipApplication;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipApproval;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Full admin/superadmin view — includes internal notes and per-admin votes. */
public record AdminScholarshipApplicationDto(
        UUID id,
        String referenceNumber,
        UserDto member,
        ScholarshipProgramDto program,
        ScholarshipApplicationStatus status,
        String beneficiaryName,
        String institutionName,
        String courseOfStudy,
        String academicYear,
        String personalStatement,
        List<String> documentUrls,
        String rejectionReason,
        String adminNotes,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime approvedAt,
        List<ApprovalSummary> approvals,
        ScholarshipApplicationTrackDto.AwardSummary award
) {

    public record ApprovalSummary(
            UUID id,
            String adminName,
            ApprovalDecision decision,
            String comment,
            LocalDateTime decidedAt
    ) {
        public static ApprovalSummary forSuperAdmin(ScholarshipApproval a) {
            return new ApprovalSummary(a.getId(), a.getAdmin().getFullName(),
                    a.getDecision(), a.getComment(), a.getDecidedAt());
        }

        public static ApprovalSummary forAdmin(ScholarshipApproval a) {
            return new ApprovalSummary(a.getId(), null,
                    a.getDecision(), null, a.getDecidedAt());
        }
    }

    public static AdminScholarshipApplicationDto from(ScholarshipApplication a, boolean isSuperAdmin) {
        List<ApprovalSummary> approvalSummaries = a.getApprovals().stream()
                .map(ap -> isSuperAdmin
                        ? ApprovalSummary.forSuperAdmin(ap)
                        : ApprovalSummary.forAdmin(ap))
                .toList();

        ScholarshipApplicationTrackDto.AwardSummary awardSummary = null;
        if (a.getAward() != null) {
            var aw = a.getAward();
            awardSummary = new ScholarshipApplicationTrackDto.AwardSummary(
                    aw.getAmountAwarded(), aw.getCurrency(),
                    aw.getMethod().name(), aw.getAwardedAt()
            );
        }

        return new AdminScholarshipApplicationDto(
                a.getId(), a.getReferenceNumber(),
                UserDto.from(a.getMember()),
                ScholarshipProgramDto.from(a.getProgram()),
                a.getStatus(), a.getBeneficiaryName(),
                a.getInstitutionName(), a.getCourseOfStudy(), a.getAcademicYear(),
                a.getPersonalStatement(), a.getDocumentUrls(),
                a.getRejectionReason(),
                isSuperAdmin ? a.getAdminNotes() : null,
                a.getSubmittedAt(), a.getReviewedAt(), a.getApprovedAt(),
                approvalSummaries, awardSummary
        );
    }
}
