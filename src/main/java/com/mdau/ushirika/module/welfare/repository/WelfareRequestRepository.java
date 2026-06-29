package com.mdau.ushirika.module.welfare.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.welfare.entity.WelfareRequest;
import com.mdau.ushirika.module.welfare.enums.WelfareRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WelfareRequestRepository extends JpaRepository<WelfareRequest, UUID> {

    Optional<WelfareRequest> findByReferenceNumber(String referenceNumber);

    Page<WelfareRequest> findAllByMemberOrderByCreatedAtDesc(User member, Pageable pageable);

    Page<WelfareRequest> findAllByStatusOrderByCreatedAtDesc(WelfareRequestStatus status, Pageable pageable);

    Page<WelfareRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(WelfareRequestStatus status);

    /** Total amount disbursed across all DISBURSED requests — for dashboard stats. */
    @Query("SELECT COALESCE(SUM(r.amountRequested), 0) FROM WelfareRequest r WHERE r.status = 'APPROVED' OR r.status = 'DISBURSED'")
    BigDecimal sumApprovedAndDisbursed();
}
