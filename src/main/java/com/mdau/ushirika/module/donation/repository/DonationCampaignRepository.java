package com.mdau.ushirika.module.donation.repository;

import com.mdau.ushirika.module.donation.entity.DonationCampaign;
import com.mdau.ushirika.module.donation.enums.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface DonationCampaignRepository extends JpaRepository<DonationCampaign, UUID> {

    /** Public listing — ACTIVE campaigns open to all. */
    Page<DonationCampaign> findAllByStatusAndIsPublicTrueOrderByCreatedAtDesc(
            CampaignStatus status, Pageable pageable);

    /** Admin listing — all statuses. */
    Page<DonationCampaign> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(CampaignStatus status);

    /** Total confirmed donations for a campaign (used in DTO enrichment). */
    @Query("""
        SELECT COALESCE(SUM(d.amount), 0)
        FROM Donation d
        WHERE d.campaign.id = :campaignId
        AND d.status = 'COMPLETED'
        """)
    BigDecimal sumCompletedDonations(@Param("campaignId") UUID campaignId);
}
