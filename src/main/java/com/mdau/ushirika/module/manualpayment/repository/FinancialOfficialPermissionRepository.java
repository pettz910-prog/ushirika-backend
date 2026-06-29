package com.mdau.ushirika.module.manualpayment.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.manualpayment.entity.FinancialOfficialPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialOfficialPermissionRepository extends JpaRepository<FinancialOfficialPermission, UUID> {

    Optional<FinancialOfficialPermission> findByOfficial(User official);

    boolean existsByOfficial(User official);

    List<FinancialOfficialPermission> findAllByOrderByCreatedAtDesc();
}
