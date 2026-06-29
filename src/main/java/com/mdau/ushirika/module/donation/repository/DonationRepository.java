package com.mdau.ushirika.module.donation.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.donation.entity.Donation;
import com.mdau.ushirika.module.donation.entity.DonationCampaign;
import com.mdau.ushirika.module.donation.enums.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    Optional<Donation> findByStripeSessionId(String sessionId);

    Page<Donation> findAllByDonorOrderByCreatedAtDesc(User donor, Pageable pageable);

    Page<Donation> findAllByCampaignOrderByCreatedAtDesc(DonationCampaign campaign, Pageable pageable);

    Page<Donation> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Donation> findAllByStatusOrderByCreatedAtDesc(DonationStatus status, Pageable pageable);

    Page<Donation> findAllByCampaignAndStatusOrderByCreatedAtDesc(
            DonationCampaign campaign, DonationStatus status, Pageable pageable);

    /** Dashboard total — all confirmed donations. */
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.status = 'COMPLETED'")
    BigDecimal sumAllCompleted();

    long countByStatus(DonationStatus status);

    @Query(value = """
        SELECT EXTRACT(YEAR  FROM donated_at)::int  AS yr,
               EXTRACT(MONTH FROM donated_at)::int  AS mo,
               SUM(amount)                          AS total,
               COUNT(*)                             AS cnt
        FROM donations
        WHERE status = 'COMPLETED'
          AND donated_at >= :from
        GROUP BY yr, mo
        ORDER BY yr, mo
        """, nativeQuery = true)
    java.util.List<Object[]> monthlyTotals(java.time.LocalDateTime from);

    /** Per-donor total for display. */
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0) FROM Donation d
        WHERE d.donor.id = :donorId AND d.status = 'COMPLETED'
        """)
    BigDecimal sumCompletedByDonor(@Param("donorId") UUID donorId);
}
