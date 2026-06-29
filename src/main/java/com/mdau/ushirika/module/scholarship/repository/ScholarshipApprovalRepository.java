package com.mdau.ushirika.module.scholarship.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipApplication;
import com.mdau.ushirika.module.scholarship.entity.ScholarshipApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScholarshipApprovalRepository extends JpaRepository<ScholarshipApproval, UUID> {

    boolean existsByApplicationAndAdmin(ScholarshipApplication application, User admin);

    long countByApplicationAndDecision(ScholarshipApplication application, ApprovalDecision decision);
}
