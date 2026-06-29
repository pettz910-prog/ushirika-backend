package com.mdau.ushirika.module.scholarship.repository;

import com.mdau.ushirika.module.scholarship.entity.ScholarshipProgram;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipProgramStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScholarshipProgramRepository extends JpaRepository<ScholarshipProgram, UUID> {

    List<ScholarshipProgram> findAllByStatusOrderByApplicationDeadlineAsc(ScholarshipProgramStatus status);

    Page<ScholarshipProgram> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
