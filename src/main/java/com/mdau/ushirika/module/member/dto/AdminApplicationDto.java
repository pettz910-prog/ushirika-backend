package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.auth.dto.UserDto;
import com.mdau.ushirika.module.member.entity.ApplicationApproval;
import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Full admin/superadmin view — includes internal notes and approval records. */
public record AdminApplicationDto(
        UUID id,
        String referenceNumber,
        UserDto applicant,
        ApplicationStatus status,
        List<String> documentUrls,
        String rejectionReason,
        String adminNotes,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime approvedAt,
        List<ApprovalSummary> approvals
) {

    /**
     * SUPERADMIN sees full detail (admin name + comment).
     * Regular ADMIN sees vote outcome only — no peer names, no comments (anonymity).
     */
    public record ApprovalSummary(
            UUID id,
            String adminName,    // null when viewing as ADMIN (peer anonymity)
            ApprovalDecision decision,
            String comment,      // null when viewing as ADMIN
            LocalDateTime decidedAt
    ) {
        public static ApprovalSummary forSuperAdmin(ApplicationApproval a) {
            return new ApprovalSummary(
                    a.getId(),
                    a.getAdmin().getFullName(),
                    a.getDecision(),
                    a.getComment(),
                    a.getDecidedAt()
            );
        }

        public static ApprovalSummary forAdmin(ApplicationApproval a) {
            return new ApprovalSummary(
                    a.getId(),
                    null,
                    a.getDecision(),
                    null,
                    a.getDecidedAt()
            );
        }
    }

    public static AdminApplicationDto from(MembershipApplication app, boolean isSuperAdmin) {
        List<ApprovalSummary> approvalSummaries = app.getApprovals().stream()
                .map(a -> isSuperAdmin
                        ? ApprovalSummary.forSuperAdmin(a)
                        : ApprovalSummary.forAdmin(a))
                .toList();

        return new AdminApplicationDto(
                app.getId(),
                app.getReferenceNumber(),
                UserDto.from(app.getUser()),
                app.getStatus(),
                app.getDocumentUrls(),
                app.getRejectionReason(),
                isSuperAdmin ? app.getAdminNotes() : null,
                app.getSubmittedAt(),
                app.getReviewedAt(),
                app.getApprovedAt(),
                approvalSummaries
        );
    }
}
