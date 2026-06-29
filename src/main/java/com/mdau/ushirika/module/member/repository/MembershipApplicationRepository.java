package com.mdau.ushirika.module.member.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.entity.MembershipApplication;
import com.mdau.ushirika.module.member.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MembershipApplicationRepository extends JpaRepository<MembershipApplication, UUID> {

    Optional<MembershipApplication> findByReferenceNumber(String referenceNumber);

    Optional<MembershipApplication> findByUser(User user);

    Page<MembershipApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);

    Page<MembershipApplication> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(ApplicationStatus status);
}
