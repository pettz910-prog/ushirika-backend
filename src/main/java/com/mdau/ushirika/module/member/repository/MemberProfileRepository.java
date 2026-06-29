package com.mdau.ushirika.module.member.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, UUID> {

    Optional<MemberProfile> findByUser(User user);

    Optional<MemberProfile> findByMemberId(String memberId);

    boolean existsByIdNumber(String idNumber);

    long countByMemberIdNotNull();
}
