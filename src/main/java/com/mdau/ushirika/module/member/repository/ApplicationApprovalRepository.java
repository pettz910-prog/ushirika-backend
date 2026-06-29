package com.mdau.ushirika.module.member.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.entity.ApplicationApproval;
import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationApprovalRepository extends JpaRepository<ApplicationApproval, UUID> {

    List<ApplicationApproval> findAllByApplication(MembershipApplication application);

    Optional<ApplicationApproval> findByApplicationAndAdmin(MembershipApplication application, User admin);

    boolean existsByApplicationAndAdmin(MembershipApplication application, User admin);

    long countByApplicationAndDecision(MembershipApplication application, ApprovalDecision decision);
}
