package com.mdau.ushirika.module.mgr.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.mgr.entity.MgrCycle;
import com.mdau.ushirika.module.mgr.entity.MgrSlot;
import com.mdau.ushirika.module.mgr.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgrSlotRepository extends JpaRepository<MgrSlot, UUID> {

    List<MgrSlot> findByCycleOrderBySlotNumber(MgrCycle cycle);

    Optional<MgrSlot> findByCycleAndUser(MgrCycle cycle, User user);

    Optional<MgrSlot> findByUser(User user);

    boolean existsByCycleAndUser(MgrCycle cycle, User user);

    int countByCycle(MgrCycle cycle);

    long countByCycleAndStatus(MgrCycle cycle, SlotStatus status);

    /** Slots drawn for a specific payout month (payoutMonth is now nullable). */
    List<MgrSlot> findByCycleAndPayoutMonth(MgrCycle cycle, Integer payoutMonth);

    /** All SCHEDULED slots with no payout month yet — candidates for monthly draw. */
    @Query("SELECT s FROM MgrSlot s WHERE s.cycle = :cycle AND s.status = :status AND s.payoutMonth IS NULL")
    List<MgrSlot> findUndrawnByCycle(@Param("cycle") MgrCycle cycle,
                                     @Param("status") SlotStatus status);
}
