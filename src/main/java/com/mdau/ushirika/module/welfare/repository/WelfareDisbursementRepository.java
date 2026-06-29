package com.mdau.ushirika.module.welfare.repository;

import com.mdau.ushirika.module.welfare.entity.WelfareDisbursement;
import com.mdau.ushirika.module.welfare.entity.WelfareRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WelfareDisbursementRepository extends JpaRepository<WelfareDisbursement, UUID> {

    Optional<WelfareDisbursement> findByWelfareRequest(WelfareRequest request);

    boolean existsByWelfareRequest(WelfareRequest request);

    @Query("SELECT COALESCE(SUM(d.amountDisbursed), 0) FROM WelfareDisbursement d")
    BigDecimal sumTotalDisbursed();
}
