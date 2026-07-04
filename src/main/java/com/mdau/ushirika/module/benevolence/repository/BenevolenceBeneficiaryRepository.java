package com.mdau.ushirika.module.benevolence.repository;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceBeneficiary;
import com.mdau.ushirika.module.benevolence.entity.BenevolenceEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BenevolenceBeneficiaryRepository extends JpaRepository<BenevolenceBeneficiary, UUID> {
    List<BenevolenceBeneficiary> findByEnrollment(BenevolenceEnrollment enrollment);
    int countByEnrollment(BenevolenceEnrollment enrollment);
}
