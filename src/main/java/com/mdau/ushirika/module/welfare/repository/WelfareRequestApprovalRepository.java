package com.mdau.ushirika.module.welfare.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import com.mdau.ushirika.module.welfare.entity.WelfareRequest;
import com.mdau.ushirika.module.welfare.entity.WelfareRequestApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WelfareRequestApprovalRepository extends JpaRepository<WelfareRequestApproval, UUID> {

    boolean existsByWelfareRequestAndAdmin(WelfareRequest request, User admin);

    long countByWelfareRequestAndDecision(WelfareRequest request, ApprovalDecision decision);

    List<WelfareRequestApproval> findAllByWelfareRequest(WelfareRequest request);
}
