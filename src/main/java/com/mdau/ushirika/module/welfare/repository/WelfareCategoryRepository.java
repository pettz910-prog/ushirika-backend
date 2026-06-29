package com.mdau.ushirika.module.welfare.repository;

import com.mdau.ushirika.module.welfare.entity.WelfareCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WelfareCategoryRepository extends JpaRepository<WelfareCategory, UUID> {

    List<WelfareCategory> findAllByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);
}
