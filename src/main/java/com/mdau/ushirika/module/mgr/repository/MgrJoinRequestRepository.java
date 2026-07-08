package com.mdau.ushirika.module.mgr.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.mgr.entity.MgrCycle;
import com.mdau.ushirika.module.mgr.entity.MgrJoinRequest;
import com.mdau.ushirika.module.mgr.enums.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MgrJoinRequestRepository extends JpaRepository<MgrJoinRequest, UUID> {

    Optional<MgrJoinRequest> findByCycleAndUser(MgrCycle cycle, User user);

    boolean existsByCycleAndUser(MgrCycle cycle, User user);

    List<MgrJoinRequest> findByCycleOrderByCreatedAtDesc(MgrCycle cycle);

    List<MgrJoinRequest> findByCycleAndStatusOrderByCreatedAtDesc(MgrCycle cycle, JoinRequestStatus status);

    List<MgrJoinRequest> findByUserOrderByCreatedAtDesc(User user);
}
