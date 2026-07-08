package com.mdau.ushirika.module.benevolence.repository;

import com.mdau.ushirika.module.benevolence.entity.BenevolenceClaimCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BenevolenceClaimCategoryRepository extends JpaRepository<BenevolenceClaimCategory, UUID> {
    List<BenevolenceClaimCategory> findAllByOrderBySortOrderAscNameAsc();
    List<BenevolenceClaimCategory> findAllByActiveTrueOrderBySortOrderAscNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
