package com.mdau.ushirika.module.attendance.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ConflictException;
import com.mdau.ushirika.common.exception.ForbiddenException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.attendance.dto.*;
import com.mdau.ushirika.module.attendance.entity.Fine;
import com.mdau.ushirika.module.attendance.entity.FinePayment;
import com.mdau.ushirika.module.attendance.enums.FinePaymentStatus;
import com.mdau.ushirika.module.attendance.enums.FineStatus;
import com.mdau.ushirika.module.attendance.repository.FinePaymentRepository;
import com.mdau.ushirika.module.attendance.repository.FineRepository;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.notification.service.EmailService;
import com.mdau.ushirika.module.payment.enums.PaymentMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinePaymentService {

    private final FinePaymentRepository finePaymentRepository;
    private final FineRepository fineRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.site-url:https://ushirikacommunity.site}")
    private String siteUrl;

    // ── Member: submit payment for a specific fine ───────────────────────────

    @Transactional
    public FinePaymentDto submit(UUID fineId, SubmitFinePaymentRequest req) {
        User member = currentUser();
        Fine fine = findFineById(fineId);

        if (!fine.getUser().getId().equals(member.getId())) {
            throw new ForbiddenException("You can only submit payments for your own fines.");
        }
        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new BadRequestException("This fine has been waived — no payment needed.");
        }
        if (fine.getStatus() == FineStatus.PAID) {
            throw new BadRequestException("This fine is already paid.");
        }

        // One PENDING submission per fine at a time — prevents duplicate queuing
        if (finePaymentRepository.findByFineAndStatus(fine, FinePaymentStatus.PENDING).isPresent()) {
            throw new ConflictException(
                "You already have a pending payment submission for this fine. " +
                "Wait for it to be processed, or contact support if it has been lost.");
        }

        if (finePaymentRepository.existsByMemberTxReferenceIgnoreCase(req.memberTxReference())) {
            throw new ConflictException(
                "This transaction reference has already been submitted. " +
                "Each transaction reference can only be used once.");
        }

        FinePayment payment = FinePayment.builder()
                .fine(fine)
                .member(member)
                .amount(req.amount())
                .paymentMode(req.paymentMode())
                .memberTxReference(req.memberTxReference().strip())
                .notes(req.notes())
                .build();

        finePaymentRepository.save(payment);
        log.info("Fine payment submitted: fineId={} member={} amount={}",
                fineId, member.getEmail(), req.amount());

        return FinePaymentDto.memberView(payment);
    }

    // ── Member: view own fine payment submissions ────────────────────────────

    @Transactional(readOnly = true)
    public List<FinePaymentDto> myPayments() {
        User member = currentUser();
        return finePaymentRepository.findAllByMemberOrderByCreatedAtDesc(member).stream()
                .map(FinePaymentDto::memberView)
                .toList();
    }

    // ── Admin: list all fine payment submissions ─────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<FinePaymentDto> listAll(FinePaymentStatus status, Pageable pageable) {
        if (status != null) {
            return PagedResponse.of(
                finePaymentRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
                    .map(FinePaymentDto::from));
        }
        return PagedResponse.of(
            finePaymentRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(FinePaymentDto::from));
    }

    // ── Admin: verify ────────────────────────────────────────────────────────

    /**
     * Admin enters the TX reference from their own Zelle/Venmo/CashApp.
     * If it matches the member's reference (case-insensitive), the submission is
     * verified and the fine is marked PAID.
     */
    @Transactional
    public FinePaymentDto verify(UUID id, VerifyFinePaymentRequest req) {
        User admin = currentUser();
        FinePayment payment = findById(id);

        if (payment.getStatus() != FinePaymentStatus.PENDING) {
            throw new BadRequestException(
                "Only PENDING submissions can be verified. Current status: " + payment.getStatus());
        }
        if (payment.getFine().getStatus() != FineStatus.PENDING) {
            throw new BadRequestException(
                "The associated fine is no longer pending (status: " +
                payment.getFine().getStatus() + "). Cannot verify.");
        }

        if (!payment.getMemberTxReference().strip()
                    .equalsIgnoreCase(req.adminTxReference().strip())) {
            throw new BadRequestException(
                "Transaction reference mismatch. The reference you entered (" +
                req.adminTxReference().strip() + ") does not match what the member submitted. " +
                "Please check your " + label(payment.getPaymentMode()) + " app and try again, " +
                "or reject this submission if the payment was not received.");
        }

        payment.setAdminTxReference(req.adminTxReference().strip());
        payment.setStatus(FinePaymentStatus.VERIFIED);
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());
        finePaymentRepository.save(payment);

        Fine fine = payment.getFine();
        fine.setStatus(FineStatus.PAID);
        fine.setPaidAt(LocalDateTime.now());
        fineRepository.save(fine);

        log.info("Fine payment verified: paymentId={} fineId={} member={} amount={} by={}",
                id, fine.getId(), payment.getMember().getEmail(), payment.getAmount(), admin.getEmail());

        sendVerifiedEmail(payment);
        return FinePaymentDto.from(payment);
    }

    // ── Admin: reject ────────────────────────────────────────────────────────

    @Transactional
    public FinePaymentDto reject(UUID id, RejectFinePaymentRequest req) {
        User admin = currentUser();
        FinePayment payment = findById(id);

        if (payment.getStatus() != FinePaymentStatus.PENDING) {
            throw new BadRequestException(
                "Only PENDING submissions can be rejected. Current status: " + payment.getStatus());
        }

        payment.setStatus(FinePaymentStatus.REJECTED);
        payment.setRejectionReason(req.reason());
        payment.setVerifiedBy(admin);
        payment.setVerifiedAt(LocalDateTime.now());
        finePaymentRepository.save(payment);

        log.info("Fine payment rejected: paymentId={} fineId={} member={} reason={} by={}",
                id, payment.getFine().getId(), payment.getMember().getEmail(), req.reason(), admin.getEmail());

        sendRejectedEmail(payment, req.reason());
        return FinePaymentDto.from(payment);
    }

    // ── Email notifications ──────────────────────────────────────────────────

    private void sendVerifiedEmail(FinePayment p) {
        String name   = p.getMember().getFullName();
        String email  = p.getMember().getEmail();
        String method = label(p.getPaymentMode());
        String amount = "$" + p.getAmount().toPlainString();
        String html = """
            <div style="font-family:sans-serif;max-width:520px;margin:auto">
              <h2 style="color:#1A4731">Fine Payment Verified ✓</h2>
              <p>Hi %s,</p>
              <p>Your <strong>%s</strong> payment of <strong>%s</strong> for the fine
                 "<em>%s</em>" has been verified. Your fine is now cleared.</p>
              <p style="color:#555">Transaction reference: <strong>%s</strong></p>
              <p>You may log into your portal to download an official receipt.</p>
              <p>— Ushirika Welfare Team</p>
            </div>
            """.formatted(name, method, amount, p.getFine().getReason(), p.getMemberTxReference());
        emailService.sendPlain(email, name, "Fine Payment Verified — Ushirika Welfare", html);
    }

    private void sendRejectedEmail(FinePayment p, String reason) {
        String name   = p.getMember().getFullName();
        String email  = p.getMember().getEmail();
        String method = label(p.getPaymentMode());
        String portal = siteUrl + "/portal";
        String html = """
            <div style="font-family:sans-serif;max-width:520px;margin:auto">
              <h2 style="color:#B91C1C">Fine Payment Could Not Be Verified</h2>
              <p>Hi %s,</p>
              <p>Your <strong>%s</strong> payment submission for the fine
                 "<em>%s</em>" (reference: <strong>%s</strong>) could not be verified.</p>
              <p><strong>Reason:</strong> %s</p>
              <p>If you have already sent the payment, please log into your portal and
                 re-submit the correct transaction reference from your %s app.</p>
              <p>
                <a href="%s"
                   style="display:inline-block;background:#1A4731;color:#fff;padding:10px 22px;
                          border-radius:999px;text-decoration:none;font-weight:600">
                  Re-submit in Portal
                </a>
              </p>
              <p>— Ushirika Welfare Team</p>
            </div>
            """.formatted(name, method, p.getFine().getReason(),
                          p.getMemberTxReference(), reason, method, portal);
        emailService.sendPlain(email, name,
            "Action Required: Re-submit Fine Payment — Ushirika Welfare", html);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String label(PaymentMode mode) {
        return switch (mode) {
            case ZELLE   -> "Zelle";
            case VENMO   -> "Venmo";
            case CASHAPP -> "CashApp";
        };
    }

    private FinePayment findById(UUID id) {
        return finePaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine payment submission not found: " + id));
    }

    private Fine findFineById(UUID id) {
        return fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
}
