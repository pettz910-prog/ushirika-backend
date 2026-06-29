package com.mdau.ushirika.module.payment.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.payment.entity.MemberContribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface MemberContributionRepository extends JpaRepository<MemberContribution, UUID> {

    Page<MemberContribution> findAllByMember(User member, Pageable pageable);

    Page<MemberContribution> findAllByOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByPaymentSessionId(String sessionId);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM MemberContribution c WHERE c.member = :member")
    BigDecimal sumByMember(User member);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM MemberContribution c")
    BigDecimal sumAll();

    /**
     * Monthly contribution totals for the last N months.
     * Returns rows of [year, month, total].
     */
    @Query(value = """
        SELECT EXTRACT(YEAR  FROM created_at)::int  AS yr,
               EXTRACT(MONTH FROM created_at)::int  AS mo,
               SUM(amount)                          AS total,
               COUNT(*)                             AS cnt
        FROM member_contributions
        WHERE created_at >= :from
        GROUP BY yr, mo
        ORDER BY yr, mo
        """, nativeQuery = true)
    java.util.List<Object[]> monthlyTotals(java.time.LocalDateTime from);

    Optional<MemberContribution> findByPaymentSessionId(String sessionId);

    long countByMember(User member);
}
