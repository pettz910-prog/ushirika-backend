package com.mdau.ushirika.module.scholarship.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipApplication;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipProgram;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScholarshipApplicationRepository extends JpaRepository<ScholarshipApplication, UUID> {

    Optional<ScholarshipApplication> findByReferenceNumber(String referenceNumber);

    boolean existsByMemberAndProgram(User member, ScholarshipProgram program);

    Page<ScholarshipApplication> findAllByMemberOrderByCreatedAtDesc(User member, Pageable pageable);

    Page<ScholarshipApplication> findAllByStatusOrderByCreatedAtDesc(ScholarshipApplicationStatus status, Pageable pageable);

    Page<ScholarshipApplication> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(ScholarshipApplicationStatus status);

    /** Count awarded slots for a program — used to enforce totalSlots cap. */
    long countByProgramAndStatusIn(ScholarshipProgram program, List<ScholarshipApplicationStatus> statuses);
}
