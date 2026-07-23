package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;

import java.time.LocalDateTime;

/** Applicant-facing view of onboarding progress — drives the onboarding wizard's step state. */
public record OnboardingStatusDto(
        String referenceNumber,
        ApplicationStatus status,
        boolean emailVerified,
        boolean additionalInfoSubmitted,
        boolean bylawsAccepted,
        boolean registrationSubmitted,
        LocalDateTime formSentAt
) {
    public static OnboardingStatusDto from(MembershipApplication app) {
        return new OnboardingStatusDto(
                app.getReferenceNumber(),
                app.getStatus(),
                app.getEmailReverifiedAt() != null,
                app.getAdditionalInfoDocumentUrls() != null && !app.getAdditionalInfoDocumentUrls().isEmpty(),
                app.getBylawsAcceptedAt() != null,
                app.getRegistrationSubmittedAt() != null,
                app.getFormSentAt()
        );
    }
}
