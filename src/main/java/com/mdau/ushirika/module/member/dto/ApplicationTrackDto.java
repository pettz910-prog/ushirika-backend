package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;

import java.time.LocalDateTime;

/** Safe public/member view — no admin names, no internal notes. */
public record ApplicationTrackDto(
        String referenceNumber,
        ApplicationStatus status,
        LocalDateTime submittedAt,
        LocalDateTime reviewedAt,
        LocalDateTime approvedAt,
        String rejectionReason,
        String memberId
) {
    public static ApplicationTrackDto from(MembershipApplication app, String memberId) {
        return new ApplicationTrackDto(
                app.getReferenceNumber(),
                app.getStatus(),
                app.getSubmittedAt(),
                app.getReviewedAt(),
                app.getApprovedAt(),
                app.getRejectionReason(),
                memberId
        );
    }
}
