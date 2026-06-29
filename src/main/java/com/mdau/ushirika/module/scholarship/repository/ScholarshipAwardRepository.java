package com.mdau.ushirika.module.scholarship.repository;

import com.mdau.ushirika.module.scholarship.entity.ScholarshipApplication;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipAward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface ScholarshipAwardRepository extends JpaRepository<ScholarshipAward, UUID> {

    boolean existsByApplication(ScholarshipApplication application);

    @Query("SELECT COALESCE(SUM(a.amountAwarded), 0) FROM ScholarshipAward a")
    BigDecimal sumTotalAwarded();
}
