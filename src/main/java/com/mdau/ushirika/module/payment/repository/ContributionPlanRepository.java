package com.mdau.ushirika.module.payment.repository;

import com.mdau.ushirika.module.payment.entity.ContributionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContributionPlanRepository extends JpaRepository<ContributionPlan, UUID> {

    List<ContributionPlan> findAllByActiveTrueOrderByDisplayOrderAsc();

    List<ContributionPlan> findAllByOrderByDisplayOrderAsc();

    boolean existsByName(String name);
}
