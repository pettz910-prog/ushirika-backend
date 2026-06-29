package com.mdau.ushirika.module.manualpayment.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ConflictException;
import com.mdau.ushirika.common.exception.ForbiddenException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.enums.UserRole;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.manualpayment.dto.*;
import com.mdau.ushirika.module.manualpayment.entity.FinancialOfficialPermission;
import com.mdau.ushirika.module.manualpayment.entity.ManualPayment;
import com.mdau.ushirika.module.manualpayment.entity.ManualPaymentAuditLog;
import com.mdau.ushirika.module.manualpayment.enums.AuditAction;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentCategory;
import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentStatus;
import com.mdau.ushirika.module.manualpayment.repository.FinancialOfficialPermissionRepository;
import com.mdau.ushirika.module.manualpayment.repository.ManualPaymentAuditLogRepository;
import com.mdau.ushirika.module.manualpayment.repository.ManualPaymentRepository;
import com.mdau.ushirika.module.payment.entity.MemberContribution;
import com.mdau.ushirika.module.payment.enums.ContributionSource;
import com.mdau.ushirika.module.payment.repository.MemberContributionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManualPaymentService {

    private final ManualPaymentRepository paymentRepository;
    private final ManualPaymentAuditLogRepository auditLogRepository;
    private final FinancialOfficialPermissionRepository permissionRepository;
    private final MemberContributionRepository contributionRepository;
    private final UserRepository userRepository;

    // ─── Record ──────────────────────────────────────────────────────────────────

    @Transactional
    public ManualPaymentDto record(RecordManualPaymentRequest req) {
        User actor = currentUser();

        assertCanRecord(actor);

        // CONTRIBUTION must be linked to a registered member
        if (req.category() == ManualPaymentCategory.CONTRIBUTION && req.memberId() == null) {
            throw new BadRequestException("memberId is required for CONTRIBUTION payments.");
        }

        // Non-member payer must supply at least a name
        if (req.memberId() == null && (req.payerName() == null || req.payerName().isBlank())) {
            throw new BadRequestException("payerName is required when the payer is not a registered member.");
        }

        // Duplicate receipt guard
        if (paymentRepository.existsByReceiptNumber(req.receiptNumber())) {
            throw new ConflictException("A payment with receipt number '" + req.receiptNumber() + "' already exists.");
        }

        User member = null;
        if (req.memberId() != null) {
            member = userRepository.findById(req.memberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + req.memberId()));
        }

        ManualPayment payment = ManualPayment.builder()
                .category(req.category())
                .amount(req.amount())
                .member(member)
                .payerName(req.payerName())
                .payerEmail(req.payerEmail())
                .paymentDate(req.paymentDate())
                .receiptNumber(req.receiptNumber())
                .period(req.period())
                .notes(req.notes())
                .status(ManualPaymentStatus.PENDING)
                .recordedBy(actor)
                .build();

        paymentRepository.save(payment);
        writeAuditLog(payment, actor, AuditAction.RECORDED, null, ManualPaymentStatus.PENDING, null);

        log.info("Manual payment recorded: id={} category={} amount={} by={}",
                payment.getId(), payment.getCategory(), payment.getAmount(), actor.getEmail());

        return ManualPaymentDto.from(payment);
    }

    // ─── Approve ─────────────────────────────────────────────────────────────────

    @Transactional
    public ManualPaymentDto approve(UUID id, ReviewManualPaymentRequest req) {
        User actor = currentUser();
        ManualPayment payment = findById(id);

        assertCanReview(actor, payment);

        if (payment.getStatus() != ManualPaymentStatus.PENDING) {
            throw new BadRequestException("Only PENDING payments can be approved. Current status: " + payment.getStatus());
        }

        ManualPaymentStatus previous = payment.getStatus();
        payment.setStatus(ManualPaymentStatus.APPROVED);
        payment.setApprovedBy(actor);
        paymentRepository.save(payment);

        writeAuditLog(payment, actor, AuditAction.APPROVED, previous, ManualPaymentStatus.APPROVED,
                req != null ? req.reason() : null);

        // For CONTRIBUTION payments linked to a member, create a confirmed MemberContribution
        if (payment.getCategory() == ManualPaymentCategory.CONTRIBUTION && payment.getMember() != null) {
            MemberContribution contribution = MemberContribution.builder()
                    .member(payment.getMember())
                    .plan(null)
                    .payment(null)
                    .manualPayment(payment)
                    .source(ContributionSource.MANUAL)
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .period(payment.getPeriod())
                    .notes("Manual cash payment — receipt: " + payment.getReceiptNumber())
                    .build();
            contributionRepository.save(contribution);
            log.info("MemberContribution created from manual payment: paymentId={} member={}",
                    payment.getId(), payment.getMember().getEmail());
        }

        log.info("Manual payment approved: id={} by={}", id, actor.getEmail());
        return ManualPaymentDto.from(payment);
    }

    // ─── Reject ──────────────────────────────────────────────────────────────────

    @Transactional
    public ManualPaymentDto reject(UUID id, ReviewManualPaymentRequest req) {
        User actor = currentUser();
        ManualPayment payment = findById(id);

        assertCanReview(actor, payment);

        if (payment.getStatus() != ManualPaymentStatus.PENDING) {
            throw new BadRequestException("Only PENDING payments can be rejected. Current status: " + payment.getStatus());
        }

        if (req == null || req.reason() == null || req.reason().isBlank()) {
            throw new BadRequestException("A rejection reason is required.");
        }

        ManualPaymentStatus previous = payment.getStatus();
        payment.setStatus(ManualPaymentStatus.REJECTED);
        payment.setRejectedBy(actor);
        payment.setRejectionReason(req.reason());
        paymentRepository.save(payment);

        writeAuditLog(payment, actor, AuditAction.REJECTED, previous, ManualPaymentStatus.REJECTED, req.reason());

        log.info("Manual payment rejected: id={} by={} reason={}", id, actor.getEmail(), req.reason());
        return ManualPaymentDto.from(payment);
    }

    // ─── Queries ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<ManualPaymentDto> listAll(ManualPaymentStatus status,
                                                    ManualPaymentCategory category,
                                                    Pageable pageable) {
        if (status != null) {
            return PagedResponse.of(
                    paymentRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
                            .map(ManualPaymentDto::from));
        }
        if (category != null) {
            return PagedResponse.of(
                    paymentRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable)
                            .map(ManualPaymentDto::from));
        }
        return PagedResponse.of(
                paymentRepository.findAllByOrderByCreatedAtDesc(pageable)
                        .map(ManualPaymentDto::from));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ManualPaymentDto> myRecorded(Pageable pageable) {
        User actor = currentUser();
        return PagedResponse.of(
                paymentRepository.findAllByRecordedByOrderByCreatedAtDesc(actor, pageable)
                        .map(ManualPaymentDto::from));
    }

    @Transactional(readOnly = true)
    public ManualPaymentDto getById(UUID id) {
        return ManualPaymentDto.from(findById(id));
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLog(UUID paymentId) {
        ManualPayment payment = findById(paymentId);
        return auditLogRepository.findAllByManualPaymentOrderByCreatedAtAsc(payment)
                .stream().map(AuditLogDto::from).toList();
    }

    // ─── Delegation management (FINANCIAL_ADMIN only) ─────────────────────────────

    @Transactional
    public FinancialOfficialPermissionDto setPermissions(UUID officialId, DelegatePermissionRequest req) {
        User admin = currentUser();
        assertFinancialAdmin(admin);

        User official = userRepository.findById(officialId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + officialId));

        if (official.getRole() != UserRole.FINANCIAL_OFFICIAL) {
            throw new BadRequestException("Permissions can only be delegated to users with role FINANCIAL_OFFICIAL.");
        }

        FinancialOfficialPermission permission = permissionRepository.findByOfficial(official)
                .orElse(FinancialOfficialPermission.builder()
                        .official(official)
                        .grantedBy(admin)
                        .build());

        permission.setCanRecordPayments(req.canRecordPayments());
        permission.setCanApprovePayments(req.canApprovePayments());
        permission.setGrantedBy(admin);

        permissionRepository.save(permission);

        log.info("Permission set for official={} record={} approve={} by={}",
                official.getEmail(), req.canRecordPayments(), req.canApprovePayments(), admin.getEmail());

        return FinancialOfficialPermissionDto.from(permission);
    }

    @Transactional
    public void revokePermissions(UUID officialId) {
        User admin = currentUser();
        assertFinancialAdmin(admin);

        User official = userRepository.findById(officialId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + officialId));

        FinancialOfficialPermission permission = permissionRepository.findByOfficial(official)
                .orElseThrow(() -> new ResourceNotFoundException("No permission record found for this official."));

        permissionRepository.delete(permission);
        log.info("Permissions revoked for official={} by={}", official.getEmail(), admin.getEmail());
    }

    @Transactional(readOnly = true)
    public List<FinancialOfficialPermissionDto> listAllPermissions() {
        User admin = currentUser();
        assertFinancialAdmin(admin);
        return permissionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(FinancialOfficialPermissionDto::from).toList();
    }

    // ─── Guards ───────────────────────────────────────────────────────────────────

    private void assertCanRecord(User actor) {
        if (actor.getRole() == UserRole.FINANCIAL_ADMIN) return;

        if (actor.getRole() == UserRole.FINANCIAL_OFFICIAL) {
            FinancialOfficialPermission perm = permissionRepository.findByOfficial(actor)
                    .orElseThrow(() -> new ForbiddenException(
                            "You do not have permission to record manual payments."));
            if (!perm.isCanRecordPayments()) {
                throw new ForbiddenException("You do not have permission to record manual payments.");
            }
            return;
        }

        throw new ForbiddenException("Only Financial Admins and authorised Financial Officials can record manual payments.");
    }

    private void assertCanReview(User actor, ManualPayment payment) {
        // Maker-checker: you cannot approve/reject what you recorded
        if (payment.getRecordedBy().getId().equals(actor.getId())) {
            throw new ForbiddenException("You cannot approve or reject a payment that you recorded (maker-checker rule).");
        }

        if (actor.getRole() == UserRole.FINANCIAL_ADMIN) return;

        if (actor.getRole() == UserRole.FINANCIAL_OFFICIAL) {
            FinancialOfficialPermission perm = permissionRepository.findByOfficial(actor)
                    .orElseThrow(() -> new ForbiddenException(
                            "You do not have permission to approve or reject manual payments."));
            if (!perm.isCanApprovePayments()) {
                throw new ForbiddenException("You do not have permission to approve or reject manual payments.");
            }
            return;
        }

        throw new ForbiddenException("Only Financial Admins and authorised Financial Officials can review manual payments.");
    }

    private void assertFinancialAdmin(User actor) {
        if (actor.getRole() != UserRole.FINANCIAL_ADMIN) {
            throw new ForbiddenException("Only Financial Admins can manage official permissions.");
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private ManualPayment findById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manual payment not found: " + id));
    }

    private void writeAuditLog(ManualPayment payment, User actor, AuditAction action,
                                ManualPaymentStatus previous, ManualPaymentStatus next, String note) {
        ManualPaymentAuditLog entry = ManualPaymentAuditLog.builder()
                .manualPayment(payment)
                .action(action)
                .actorName(actor.getFullName())
                .actorEmail(actor.getEmail())
                .actorRole(actor.getRole().name())
                .previousStatus(previous)
                .newStatus(next)
                .note(note)
                .build();
        auditLogRepository.save(entry);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
}
