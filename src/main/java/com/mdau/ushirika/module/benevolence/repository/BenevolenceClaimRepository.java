package com.mdau.ushirika.module.benevolence.repository;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceClaim;
import com.mdau.ushirika.module.benevolence.entity.BenevolenceEnrollment;
import com.mdau.ushirika.module.benevolence.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BenevolenceClaimRepository extends JpaRepository<BenevolenceClaim, UUID> {
    List<BenevolenceClaim> findByEnrollmentOrderBySubmittedAtDesc(BenevolenceEnrollment enrollment);
    Page<BenevolenceClaim> findAllByOrderBySubmittedAtDesc(Pageable pageable);
    Page<BenevolenceClaim> findAllByStatusOrderBySubmittedAtDesc(ClaimStatus status, Pageable pageable);
    Optional<BenevolenceClaim> findByReferenceNumber(String referenceNumber);
}
