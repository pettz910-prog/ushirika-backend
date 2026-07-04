package com.mdau.ushirika.module.mgr.repository;

import com.mdau.ushirika.module.mgr.entity.MgrContribution;
import com.mdau.ushirika.module.mgr.entity.MgrCycle;
import com.mdau.ushirika.module.mgr.entity.MgrSlot;
import com.mdau.ushirika.module.mgr.enums.ContributionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgrContributionRepository extends JpaRepository<MgrContribution, UUID> {
    List<MgrContribution> findBySlotOrderByContributionMonth(MgrSlot slot);
    List<MgrContribution> findByCycleAndContributionMonthOrderBySlotSlotNumber(MgrCycle cycle, int month);
    Optional<MgrContribution> findBySlotAndContributionMonth(MgrSlot slot, int month);
    long countByCycleAndStatus(MgrCycle cycle, ContributionStatus status);
    long countByCycleAndContributionMonthAndStatus(MgrCycle cycle, int month, ContributionStatus status);

    @Query("SELECT c FROM MgrContribution c WHERE c.cycle = :cycle AND c.status = 'PENDING' ORDER BY c.contributionMonth, c.slot.slotNumber")
    List<MgrContribution> findPendingByCycle(MgrCycle cycle);
}
