package com.mdau.ushirika.module.member.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.dto.*;
import com.mdau.ushirika.module.member.entity.ApplicationApproval;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;
import com.mdau.ushirika.module.member.repository.ApplicationApprovalRepository;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import com.mdau.ushirika.module.member.repository.MembershipApplicationRepository;
import com.mdau.ushirika.module.dues.service.MembershipDuesService;
import com.mdau.ushirika.module.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipApplicationRepository applicationRepository;
    private final MemberProfileRepository profileRepository;
    private final ApplicationApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MembershipDuesService membershipDuesService;

    // ------------------------------------------------------------------ Member

    /**
     * Create or update a DRAFT application.
     * One active application per user — rejected users may reapply.
     */
    @Transactional
    public ApplicationTrackDto saveApplication(MembershipApplicationRequest req) {
        User user = currentUser();

        List<ApplicationStatus> activeStatuses = List.of(
                ApplicationStatus.DRAFT,
                ApplicationStatus.SUBMITTED,
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.APPROVED
        );
        applicationRepository.findByUser(user).ifPresent(existing -> {
            if (activeStatuses.contains(existing.getStatus())) {
                throw new ConflictException(
                        "You already have an active application (ref: " + existing.getReferenceNumber() + "). " +
                        "You may only reapply after a rejection.");
            }
        });

        if (profileRepository.existsByIdNumber(req.idNumber())) {
            profileRepository.findByUser(user).ifPresent(p -> {
                if (!p.getIdNumber().equals(req.idNumber())) {
                    throw new ConflictException("National ID number is already registered.");
                }
            });
            if (profileRepository.findByUser(user).isEmpty()) {
                throw new ConflictException("National ID number is already registered.");
            }
        }

        MemberProfile profile = profileRepository.findByUser(user)
                .orElse(MemberProfile.builder().user(user).build());
        profile.setIdNumber(req.idNumber());
        profile.setDateOfBirth(req.dateOfBirth());
        profile.setGender(req.gender());
        profile.setAddress(req.address());
        profile.setCounty(req.county());
        profile.setMaritalStatus(req.maritalStatus());
        profile.setSpouseName(req.spouseName());
        profile.setChildrenJson(serializeChildren(req.children()));
        profile.setNextOfKinName(req.nextOfKinName());
        profile.setNextOfKinPhone(req.nextOfKinPhone());
        profile.setNextOfKinRelationship(req.nextOfKinRelationship());
        profile.setEmergencyContactName(req.emergencyContactName());
        profile.setEmergencyContactPhone(req.emergencyContactPhone());
        profile.setOccupation(req.occupation());
        profile.setEmployer(req.employer());
        profile.setReference1Name(req.reference1Name());
        profile.setReference1MemberId(req.reference1MemberId());
        profile.setReference2Name(req.reference2Name());
        profile.setReference2MemberId(req.reference2MemberId());
        profile.setHeardAboutUs(req.heardAboutUs());
        profileRepository.save(profile);

        MembershipApplication application = applicationRepository.findByUser(user)
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED || a.getStatus() == ApplicationStatus.DRAFT)
                .orElse(MembershipApplication.builder()
                        .user(user)
                        .referenceNumber(generateReferenceNumber())
                        .build());

        application.setDocumentUrls(req.documentUrls() != null ? req.documentUrls() : List.of());
        application.setStatus(ApplicationStatus.DRAFT);
        applicationRepository.save(application);

        return ApplicationTrackDto.from(application, profile.getMemberId());
    }

    @Transactional
    public ApplicationTrackDto submitApplication() {
        User user = currentUser();
        MembershipApplication application = applicationRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No application found. Please fill in your details first."));

        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT applications can be submitted. Current status: " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        applicationRepository.save(application);

        notifyAdminsOfNewApplication(application, user);

        return ApplicationTrackDto.from(application,
                profileRepository.findByUser(user).map(MemberProfile::getMemberId).orElse(null));
    }

    @Transactional(readOnly = true)
    public ApplicationTrackDto getMyApplication() {
        User user = currentUser();
        MembershipApplication app = applicationRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("You have not submitted a membership application."));
        String memberId = profileRepository.findByUser(user).map(MemberProfile::getMemberId).orElse(null);
        return ApplicationTrackDto.from(app, memberId);
    }

    @Transactional(readOnly = true)
    public ApplicationTrackDto trackByReference(String referenceNumber) {
        MembershipApplication app = applicationRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for reference: " + referenceNumber));
        String memberId = app.getUser() != null
                ? profileRepository.findByUser(app.getUser()).map(MemberProfile::getMemberId).orElse(null)
                : null;
        return ApplicationTrackDto.from(app, memberId);
    }

    @Transactional
    public ApplicationTrackDto submitPublicApplication(PublicMembershipApplicationRequest req) {
        String address = req.street() + ", " + req.city() + ", " + req.state() + " " + req.zipCode();
        MembershipApplication application = MembershipApplication.builder()
                .referenceNumber(generateReferenceNumber())
                .applicantName(req.firstName() + " " + req.lastName())
                .applicantEmail(req.email())
                .applicantPhone(req.phone())
                .applicantCounty(req.kenyaCounty())
                .applicantSubtribe(req.subtribe())
                .applicantEligibility(req.eligibility())
                .applicantAddress(address)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(LocalDateTime.now())
                .build();
        applicationRepository.save(application);
        notifyAdminsOfPublicApplication(application);
        sendApplicantConfirmation(application);
        return ApplicationTrackDto.from(application, null);
    }

    // ------------------------------------------------------------------ Admin

    @Transactional(readOnly = true)
    public PagedResponse<AdminApplicationDto> listApplications(ApplicationStatus status, Pageable pageable, boolean isSuperAdmin) {
        Page<MembershipApplication> page = status != null
                ? applicationRepository.findAllByStatus(status, pageable)
                : applicationRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PagedResponse.of(page.map(a -> AdminApplicationDto.from(a, isSuperAdmin)));
    }

    @Transactional(readOnly = true)
    public AdminApplicationDto getApplication(UUID id, boolean isSuperAdmin) {
        return AdminApplicationDto.from(findApplicationById(id), isSuperAdmin);
    }

    @Transactional
    public AdminApplicationDto review(UUID applicationId, AdminReviewRequest req, boolean isSuperAdmin) {
        User admin = currentUser();
        MembershipApplication application = findApplicationById(applicationId);

        if (!List.of(ApplicationStatus.SUBMITTED, ApplicationStatus.UNDER_REVIEW).contains(application.getStatus())) {
            throw new BadRequestException("This application has already been " + application.getStatus().name().toLowerCase() + " and cannot be changed.");
        }

        // Record the decision for audit trail
        ApplicationApproval approval = ApplicationApproval.builder()
                .application(application)
                .admin(admin)
                .decision(req.decision())
                .comment(req.comment())
                .decidedAt(LocalDateTime.now())
                .build();
        approvalRepository.saveAndFlush(approval);

        // Single-admin direct decision — quorum mode can be re-enabled later
        switch (req.decision()) {
            case APPROVED -> applyApproval(application);
            case REJECTED -> applyRejection(application);
        }

        applicationRepository.save(application);
        return AdminApplicationDto.from(application, isSuperAdmin);
    }

    // ------------------------------------------------------------------ Private

    private void applyRejection(MembershipApplication application) {
        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectionReason("Your membership application was reviewed and not approved by the board.");
        application.setReviewedAt(LocalDateTime.now());

        User applicant = application.getUser();
        if (applicant != null) {
            emailService.sendPlain(
                    applicant.getEmail(), applicant.getFullName(),
                    "Membership Application Update — Ushirika Welfare",
                    "Dear " + applicant.getFirstName() + ",\n\n" +
                    "We regret to inform you that your membership application (ref: " +
                    application.getReferenceNumber() + ") has not been approved at this time.\n\n" +
                    "You are welcome to reapply. If you have questions, please contact our office.\n\n" +
                    "Regards,\nUshirika Welfare Foundation"
            );
        } else if (application.getApplicantEmail() != null) {
            emailService.sendPlain(
                    application.getApplicantEmail(), application.getApplicantName(),
                    "Membership Enquiry Update — Ushirika Welfare",
                    "Dear " + application.getApplicantName() + ",\n\n" +
                    "We have reviewed your membership enquiry (ref: " +
                    application.getReferenceNumber() + ") and are unable to proceed at this time.\n\n" +
                    "You are welcome to reapply. If you have questions, please contact our office.\n\n" +
                    "Regards,\nUshirika Welfare Foundation"
            );
        }
        log.info("Membership application {} rejected.", application.getReferenceNumber());
    }

    private void applyApproval(MembershipApplication application) {
        application.setStatus(ApplicationStatus.APPROVED);
        application.setApprovedAt(LocalDateTime.now());
        application.setReviewedAt(LocalDateTime.now());

        User user = application.getUser();
        if (user == null) {
            // Public submission — no account exists yet. Admin must create the account manually via "Add Member".
            log.info("Public membership application {} approved. Admin must create the account via Add Member.",
                    application.getReferenceNumber());
            return;
        }

        MemberProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Member profile not found for approved application."));
        profile.setMemberId(generateMemberId());
        profile.setMemberSince(LocalDate.now());
        if (profile.getMembershipTier() == null) {
            profile.setMembershipTier("Standard");
        }
        profileRepository.save(profile);
        membershipDuesService.createInitialDues(user);

        emailService.sendPlain(
                user.getEmail(), user.getFullName(),
                "Welcome to Ushirika Welfare Foundation!",
                "Dear " + user.getFirstName() + ",\n\n" +
                "Congratulations! Your membership application has been approved.\n\n" +
                "Your Member ID: " + profile.getMemberId() + "\n\n" +
                "You are now a full member of Ushirika Welfare Foundation.\n\n" +
                "Warmly,\nUshirika Welfare Foundation"
        );
        log.info("Membership application {} approved. Member ID: {}",
                application.getReferenceNumber(), profile.getMemberId());
    }

    private void sendApplicantConfirmation(MembershipApplication application) {
        emailService.sendPlain(
                application.getApplicantEmail(), application.getApplicantName(),
                "We received your membership enquiry — Ushirika Welfare DFW",
                """
                <div style="font-family:sans-serif;max-width:560px;margin:auto;color:#1a1a1a">
                  <h2 style="color:#1A4731">Thank you, %s!</h2>
                  <p>We have received your membership enquiry and it is now under review by our committee.</p>
                  <p><strong>Your reference number is: %s</strong></p>
                  <p>You can use this reference number to track your application status at any time by visiting
                     our website and clicking <em>Track Application</em>.</p>
                  <p>The committee will be in touch within 5 business days. If you have any questions in the
                     meantime, please reply to this email.</p>
                  <p>— Ushirika Welfare Foundation DFW</p>
                </div>
                """.formatted(application.getApplicantName(), application.getReferenceNumber())
        );
    }

    private void notifyAdminsOfPublicApplication(MembershipApplication application) {
        userRepository.findAllByRoleIn(List.of(UserRole.ADMIN, UserRole.SUPERADMIN)).forEach(admin ->
                emailService.sendPlain(
                        admin.getEmail(), admin.getFullName(),
                        "New Public Membership Enquiry — Action Required",
                        "Hello " + admin.getFirstName() + ",\n\n" +
                        "A new public membership enquiry requires your review.\n\n" +
                        "Applicant: " + application.getApplicantName() + "\n" +
                        "Email: " + application.getApplicantEmail() + "\n" +
                        "Reference: " + application.getReferenceNumber() + "\n\n" +
                        "Log in to the admin portal to review this application.\n\n" +
                        "Ushirika Welfare Foundation"
                )
        );
    }

    private void notifyAdminsOfNewApplication(MembershipApplication application, User applicant) {
        userRepository.findAllByRoleIn(List.of(UserRole.ADMIN, UserRole.SUPERADMIN)).forEach(admin ->
                emailService.sendPlain(
                        admin.getEmail(), admin.getFullName(),
                        "New Membership Application — Action Required",
                        "Hello " + admin.getFirstName() + ",\n\n" +
                        "A new membership application requires your review.\n\n" +
                        "Applicant: " + applicant.getFullName() + "\n" +
                        "Reference: " + application.getReferenceNumber() + "\n\n" +
                        "Log in to the admin portal to cast your vote.\n\n" +
                        "Ushirika Welfare Foundation"
                )
        );
    }

    private MembershipApplication findApplicationById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }

    private String generateReferenceNumber() {
        return "UWF-APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateMemberId() {
        int year = LocalDate.now().getYear();
        long sequence = profileRepository.countByMemberIdNotNull() + 1;
        return "UW-%d-%04d".formatted(year, sequence);
    }

    private String serializeChildren(java.util.List<MembershipApplicationRequest.ChildRecord> children) {
        if (children == null || children.isEmpty()) return "[]";
        var sb = new StringBuilder("[");
        for (int i = 0; i < children.size(); i++) {
            var c = children.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"name\":\"").append(c.name() == null ? "" : c.name().replace("\"", "\\\""))
              .append("\",\"dateOfBirth\":\"").append(c.dateOfBirth() == null ? "" : c.dateOfBirth())
              .append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }
}
