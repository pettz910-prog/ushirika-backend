package com.mdau.ushirika.module.mgr.repository;

import com.mdau.ushirika.module.mgr.entity.MgrCycle;
import com.mdau.ushirika.module.mgr.enums.CycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgrCycleRepository extends JpaRepository<MgrCycle, UUID> {
    List<MgrCycle> findAllByOrderByYearDescStartDateDesc();
    List<MgrCycle> findAllByStatus(CycleStatus status);
    Optional<MgrCycle> findFirstByStatusOrderByStartDateDesc(CycleStatus status);
}
