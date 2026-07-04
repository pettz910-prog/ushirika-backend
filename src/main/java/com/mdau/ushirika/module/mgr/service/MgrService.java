package com.mdau.ushirika.module.mgr.service;

import com.mdau.ushirika.common.exception.BadRequestException;
import com.mdau.ushirika.common.exception.ConflictException;
import com.mdau.ushirika.common.exception.ResourceNotFoundException;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.auth.repository.UserRepository;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import com.mdau.ushirika.module.member.repository.MemberProfileRepository;
import com.mdau.ushirika.module.mgr.dto.*;
import com.mdau.ushirika.module.mgr.entity.*;
import com.mdau.ushirika.module.mgr.enums.*;
import com.mdau.ushirika.module.mgr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MgrService {

    private final MgrCycleRepository cycleRepo;
    private final MgrSlotRepository slotRepo;
    private final MgrContributionRepository contributionRepo;
    private final MemberProfileRepository profileRepo;
    private final UserRepository userRepo;

    // ── Cycles ────────────────────────────────────────────────────────────────

    @Transactional
    public MgrCycleDto createCycle(CreateCycleRequest req) {
        MgrCycle cycle = MgrCycle.builder()
                .name(req.name())
                .year(req.year())
                .startDate(req.startDate())
                .endDate(req.startDate().plusMonths(11).withDayOfMonth(
                        req.startDate().plusMonths(11).lengthOfMonth()))
                .notes(req.notes())
                .build();
        cycleRepo.save(cycle);
        return MgrCycleDto.summary(cycle, 0, 0);
    }

    @Transactional(readOnly = true)
    public List<MgrCycleDto> listCycles() {
        return cycleRepo.findAllByOrderByYearDescStartDateDesc().stream()
                .map(c -> {
                    int assigned = slotRepo.countByCycle(c);
                    long paid = slotRepo.countByCycleAndStatus(c, SlotStatus.PAID);
                    return MgrCycleDto.summary(c, assigned, paid);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MgrCycleDto getCycle(UUID id) {
        MgrCycle cycle = findCycle(id);
        return toFullDto(cycle);
    }

    @Transactional
    public MgrCycleDto updateCycle(UUID id, CreateCycleRequest req) {
        MgrCycle cycle = findCycle(id);
        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT cycles can be updated.");
        }
        cycle.setName(req.name());
        cycle.setYear(req.year());
        cycle.setStartDate(req.startDate());
        cycle.setEndDate(req.startDate().plusMonths(11).withDayOfMonth(
                req.startDate().plusMonths(11).lengthOfMonth()));
        cycle.setNotes(req.notes());
        cycleRepo.save(cycle);
        return toFullDto(cycle);
    }

    // ── Slots ─────────────────────────────────────────────────────────────────

    @Transactional
    public MgrSlotDto assignSlot(UUID cycleId, AssignSlotRequest req) {
        MgrCycle cycle = findCycle(cycleId);
        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BadRequestException("Slots can only be assigned to DRAFT cycles.");
        }
        if (slotRepo.existsByCycleAndSlotNumber(cycle, req.slotNumber())) {
            throw new ConflictException("Slot " + req.slotNumber() + " is already assigned.");
        }
        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + req.userId()));
        if (slotRepo.existsByCycleAndUser(cycle, user)) {
            throw new ConflictException("This member already has a slot in this cycle.");
        }

        int payoutMonth = (int) Math.ceil((double) req.slotNumber() / cycle.getPayoutsPerMonth());
        int payoutOrder = ((req.slotNumber() - 1) % cycle.getPayoutsPerMonth()) + 1;
        LocalDate scheduledPayoutDate = cycle.getStartDate()
                .plusMonths(payoutMonth - 1L)
                .withDayOfMonth(15);

        MgrSlot slot = MgrSlot.builder()
                .cycle(cycle)
                .user(user)
                .slotNumber(req.slotNumber())
                .payoutMonth(payoutMonth)
                .payoutOrder(payoutOrder)
                .scheduledPayoutDate(scheduledPayoutDate)
                .payoutAmount(cycle.getPayoutAmountPerSlot())
                .build();
        slotRepo.save(slot);
        return MgrSlotDto.summary(slot, memberId(user));
    }

    @Transactional
    public void removeSlot(UUID slotId) {
        MgrSlot slot = findSlot(slotId);
        if (slot.getCycle().getStatus() != CycleStatus.DRAFT) {
            throw new BadRequestException("Slots can only be removed from DRAFT cycles.");
        }
        slotRepo.delete(slot);
    }

    // ── Activate ──────────────────────────────────────────────────────────────

    @Transactional
    public MgrCycleDto activateCycle(UUID id) {
        MgrCycle cycle = findCycle(id);
        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT cycles can be activated.");
        }
        int assigned = slotRepo.countByCycle(cycle);
        if (assigned != cycle.getTotalSlots()) {
            throw new BadRequestException(
                    "All " + cycle.getTotalSlots() + " slots must be assigned before activating. "
                    + "Currently assigned: " + assigned);
        }

        // Generate contribution records: 24 slots × 12 months = 288
        List<MgrSlot> slots = slotRepo.findByCycleOrderBySlotNumber(cycle);
        List<MgrContribution> contributions = new ArrayList<>();
        for (MgrSlot slot : slots) {
            for (int month = 1; month <= 12; month++) {
                contributions.add(MgrContribution.builder()
                        .slot(slot)
                        .cycle(cycle)
                        .contributionMonth(month)
                        .amount(cycle.getMonthlyContribution())
                        .build());
            }
        }
        contributionRepo.saveAll(contributions);

        cycle.setStatus(CycleStatus.ACTIVE);
        cycle.setActivatedAt(LocalDateTime.now());
        cycleRepo.save(cycle);
        return toFullDto(cycle);
    }

    @Transactional
    public MgrCycleDto completeCycle(UUID id) {
        MgrCycle cycle = findCycle(id);
        if (cycle.getStatus() != CycleStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE cycles can be completed.");
        }
        cycle.setStatus(CycleStatus.COMPLETED);
        cycle.setCompletedAt(LocalDateTime.now());
        cycleRepo.save(cycle);
        return toFullDto(cycle);
    }

    @Transactional
    public MgrCycleDto cancelCycle(UUID id) {
        MgrCycle cycle = findCycle(id);
        if (cycle.getStatus() == CycleStatus.COMPLETED) {
            throw new BadRequestException("Completed cycles cannot be cancelled.");
        }
        cycle.setStatus(CycleStatus.CANCELLED);
        cycleRepo.save(cycle);
        return toFullDto(cycle);
    }

    // ── Contributions ─────────────────────────────────────────────────────────

    @Transactional
    public MgrContributionDto recordContribution(RecordContributionRequest req) {
        MgrSlot slot = findSlot(req.slotId());
        if (slot.getCycle().getStatus() != CycleStatus.ACTIVE) {
            throw new BadRequestException("Contributions can only be recorded for ACTIVE cycles.");
        }

        MgrContribution contribution = contributionRepo
                .findBySlotAndContributionMonth(slot, req.month())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contribution record not found for slot " + slot.getSlotNumber()
                        + " month " + req.month()));

        if (contribution.getStatus() == ContributionStatus.PAID) {
            throw new BadRequestException("This contribution is already recorded as PAID.");
        }

        contribution.setAmount(req.amount());
        contribution.setPaymentMethod(req.paymentMethod());
        contribution.setPaymentReference(req.paymentReference());
        contribution.setPaidAt(LocalDateTime.now());
        contribution.setNotes(req.notes());
        contribution.setStatus(ContributionStatus.PAID);
        contributionRepo.save(contribution);
        return MgrContributionDto.from(contribution);
    }

    @Transactional
    public MgrContributionDto waiveContribution(UUID contributionId, String reason) {
        MgrContribution contribution = contributionRepo.findById(contributionId)
                .orElseThrow(() -> new ResourceNotFoundException("Contribution not found: " + contributionId));
        if (contribution.getStatus() == ContributionStatus.PAID) {
            throw new BadRequestException("Cannot waive a PAID contribution.");
        }
        contribution.setStatus(ContributionStatus.WAIVED);
        contribution.setNotes(reason);
        contributionRepo.save(contribution);
        return MgrContributionDto.from(contribution);
    }

    @Transactional(readOnly = true)
    public List<MgrContributionDto> getMonthContributions(UUID cycleId, int month) {
        MgrCycle cycle = findCycle(cycleId);
        return contributionRepo
                .findByCycleAndContributionMonthOrderBySlotSlotNumber(cycle, month)
                .stream()
                .map(MgrContributionDto::from)
                .toList();
    }

    // ── Payouts ───────────────────────────────────────────────────────────────

    @Transactional
    public MgrSlotDto recordPayout(UUID slotId, RecordPayoutRequest req) {
        MgrSlot slot = findSlot(slotId);
        if (slot.getCycle().getStatus() != CycleStatus.ACTIVE) {
            throw new BadRequestException("Payouts can only be recorded for ACTIVE cycles.");
        }
        if (slot.getStatus() == SlotStatus.PAID) {
            throw new BadRequestException("This slot has already been paid out.");
        }
        slot.setStatus(SlotStatus.PAID);
        slot.setPaidAt(LocalDateTime.now());
        slot.setPaymentReference(req.paymentReference());
        slot.setAdminNotes(req.adminNotes());
        slotRepo.save(slot);

        // Auto-complete cycle if all slots are paid
        long remaining = slotRepo.countByCycleAndStatus(slot.getCycle(), SlotStatus.SCHEDULED);
        if (remaining == 0) {
            slot.getCycle().setStatus(CycleStatus.COMPLETED);
            slot.getCycle().setCompletedAt(LocalDateTime.now());
            cycleRepo.save(slot.getCycle());
        }

        List<MgrContributionDto> contributions = contributionRepo
                .findBySlotOrderByContributionMonth(slot).stream()
                .map(MgrContributionDto::from).toList();
        return MgrSlotDto.from(slot, memberId(slot.getUser()), contributions);
    }

    // ── Member self-service ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MgrSlotDto getMySlot(UUID cycleId) {
        User user = currentUser();
        MgrCycle cycle = cycleId != null ? findCycle(cycleId)
                : cycleRepo.findFirstByStatusOrderByStartDateDesc(CycleStatus.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException("No active MGR cycle found."));

        MgrSlot slot = slotRepo.findByCycleAndUser(cycle, user)
                .orElseThrow(() -> new ResourceNotFoundException("You are not assigned to this MGR cycle."));

        List<MgrContributionDto> contributions = contributionRepo
                .findBySlotOrderByContributionMonth(slot).stream()
                .map(MgrContributionDto::from).toList();
        return MgrSlotDto.from(slot, memberId(user), contributions);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MgrCycleDto toFullDto(MgrCycle cycle) {
        List<MgrSlot> slots = slotRepo.findByCycleOrderBySlotNumber(cycle);
        int assigned = slots.size();
        long paidPayouts = slots.stream().filter(s -> s.getStatus() == SlotStatus.PAID).count();
        long pendingContribs = contributionRepo.countByCycleAndStatus(cycle, ContributionStatus.PENDING);

        List<MgrSlotDto> slotDtos = slots.stream()
                .map(s -> {
                    List<MgrContributionDto> contribs = contributionRepo
                            .findBySlotOrderByContributionMonth(s).stream()
                            .map(MgrContributionDto::from).toList();
                    return MgrSlotDto.from(s, memberId(s.getUser()), contribs);
                })
                .toList();

        return MgrCycleDto.from(cycle, assigned, paidPayouts, pendingContribs, slotDtos);
    }

    private String memberId(User user) {
        return profileRepo.findByUser(user)
                .map(MemberProfile::getMemberId)
                .orElse(null);
    }

    private MgrCycle findCycle(UUID id) {
        return cycleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MGR cycle not found: " + id));
    }

    private MgrSlot findSlot(UUID id) {
        return slotRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MGR slot not found: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
