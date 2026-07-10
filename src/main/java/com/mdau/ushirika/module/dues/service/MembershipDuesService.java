package com.mdau.ushirika.module.dues.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.audit.service.AuditLogService;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.dues.dto.MembershipDueDto;
import com.mdau.ushirika.module.dues.dto.RecordDuesPaymentRequest;
import com.mdau.ushirika.module.dues.dto.WaiveDuesRequest;
import com.mdau.ushirika.module.dues.entity.MembershipDue;
import com.mdau.ushirika.module.dues.enums.DuesStatus;
import com.mdau.ushirika.module.dues.repository.MembershipDueRepository;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipDuesService {

    static final BigDecimal ANNUAL_FEE = new BigDecimal("100.00");

    private final MembershipDueRepository dueRepository;
    private final MemberProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    // ── Called on member approval ─────────────────────────────────────────────

    @Transactional
    public void createInitialDues(User user) {
        int year = LocalDate.now().getYear();
        if (dueRepository.findByUserAndYear(user, year).isPresent()) return;

        // Give new members 30 days from approval to pay
        LocalDate dueDate = LocalDate.now().plusDays(30);

        MembershipDue due = MembershipDue.builder()
                .user(user)
                .year(year)
                .amount(ANNUAL_FEE)
                .dueDate(dueDate)
                .status(DuesStatus.PENDING)
                .build();
        dueRepository.save(due);
        log.info("Created initial dues record for user {} year {}", user.getId(), year);
    }

    // ── Current year status (used for profile status derivation) ─────────────

    @Transactional(readOnly = true)
    public Optional<DuesStatus> getCurrentYearStatus(User user) {
        return dueRepository.findByUserAndYear(user, LocalDate.now().getYear())
                .map(MembershipDue::getStatus);
    }

    // ── Admin operations ──────────────────────────────────────────────────────

    @Transactional
    public MembershipDueDto recordPayment(RecordDuesPaymentRequest req) {
        User user = userRepository.findById(UUID.fromString(req.userId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.userId()));

        MembershipDue due = dueRepository.findByUserAndYear(user, req.year())
                .orElseGet(() -> {
                    // Admin can retroactively record payment and create the record
                    return MembershipDue.builder()
                            .user(user)
                            .year(req.year())
                            .amount(ANNUAL_FEE)
                            .dueDate(LocalDate.of(req.year(), 1, 31))
                            .status(DuesStatus.PENDING)
                            .build();
                });

        if (due.getStatus() == DuesStatus.PAID) {
            throw new BadRequestException("Dues for " + req.year() + " are already marked PAID.");
        }

        due.setStatus(DuesStatus.PAID);
        due.setPaidAt(LocalDateTime.now());
        due.setPaymentMethod(req.paymentMethod());
        due.setPaymentReference(req.paymentReference());
        due.setNotes(req.notes());
        dueRepository.save(due);

        log.info("Dues paid: user={} year={} method={}", user.getId(), req.year(), req.paymentMethod());
        MembershipDueDto dto = MembershipDueDto.from(due, memberId(user));
        auditLogService.log(currentUser(), "DUES_RECORDED", "MembershipDue", due.getId(),
                String.format("Dues recorded for %s — year %d via %s", user.getFullName(), req.year(), req.paymentMethod()));
        return dto;
    }

    @Transactional
    public MembershipDueDto waiveDues(UUID dueId, WaiveDuesRequest req) {
        MembershipDue due = findById(dueId);
        if (due.getStatus() == DuesStatus.PAID) {
            throw new BadRequestException("Cannot waive dues that are already PAID.");
        }
        due.setStatus(DuesStatus.WAIVED);
        due.setNotes(req != null && req.reason() != null ? req.reason() : due.getNotes());
        dueRepository.save(due);
        auditLogService.log(currentUser(), "DUES_WAIVED", "MembershipDue", due.getId(),
                "Waived dues for " + due.getUser().getFullName() + " — year " + due.getYear());
        return MembershipDueDto.from(due, memberId(due.getUser()));
    }

    @Transactional
    public int assessOverdue() {
        List<MembershipDue> overdue = dueRepository.findOverdue(LocalDate.now());
        overdue.forEach(d -> d.setStatus(DuesStatus.OVERDUE));
        dueRepository.saveAll(overdue);
        log.info("Marked {} dues records as OVERDUE", overdue.size());
        return overdue.size();
    }

    @Transactional(readOnly = true)
    public PagedResponse<MembershipDueDto> listAll(Integer year, DuesStatus status, Pageable pageable) {
        Page<MembershipDue> page;
        if (year != null && status != null) {
            page = dueRepository.findAllByYearAndStatusOrderByCreatedAtDesc(year, status, pageable);
        } else if (year != null) {
            page = dueRepository.findAllByYearOrderByCreatedAtDesc(year, pageable);
        } else if (status != null) {
            page = dueRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            page = dueRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return PagedResponse.of(page.map(d -> MembershipDueDto.from(d, memberId(d.getUser()))));
    }

    @Transactional(readOnly = true)
    public List<MembershipDueDto> getMemberDuesHistory(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        String mid = memberId(user);
        return dueRepository.findByUserOrderByYearDesc(user).stream()
                .map(d -> MembershipDueDto.from(d, mid))
                .toList();
    }

    // ── Member operations ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MembershipDueDto> getMyDues() {
        User user = currentUser();
        String mid = memberId(user);
        return dueRepository.findByUserOrderByYearDesc(user).stream()
                .map(d -> MembershipDueDto.from(d, mid))
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MembershipDue findById(UUID id) {
        return dueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dues record not found: " + id));
    }

    private String memberId(User user) {
        return profileRepository.findByUser(user)
                .map(MemberProfile::getMemberId)
                .orElse(null);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
}
