package com.mdau.ushirika.module.mgr.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.mgr.entity.MgrCycle;
import com.mdau.ushirika.module.mgr.entity.MgrSlot;
import com.mdau.ushirika.module.mgr.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgrSlotRepository extends JpaRepository<MgrSlot, UUID> {
    List<MgrSlot> findByCycleOrderBySlotNumber(MgrCycle cycle);
    Optional<MgrSlot> findByCycleAndUser(MgrCycle cycle, User user);
    Optional<MgrSlot> findByUser(User user);
    boolean existsByCycleAndUser(MgrCycle cycle, User user);
    boolean existsByCycleAndSlotNumber(MgrCycle cycle, int slotNumber);
    int countByCycle(MgrCycle cycle);
    long countByCycleAndStatus(MgrCycle cycle, SlotStatus status);
    List<MgrSlot> findByCycleAndPayoutMonth(MgrCycle cycle, int payoutMonth);
}
