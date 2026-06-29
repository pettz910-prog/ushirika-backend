package com.mdau.ushirika.module.donation.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.donation.enums.CampaignStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "donation_campaigns",
    indexes = {
        @Index(name = "idx_dc_status",          columnList = "status"),
        @Index(name = "idx_dc_is_public",       columnList = "is_public"),
        // Primary listing query: ACTIVE public campaigns
        @Index(name = "idx_dc_status_public",   columnList = "status, is_public"),
        @Index(name = "idx_dc_end_date",        columnList = "end_date"),
        @Index(name = "idx_dc_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationCampaign extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Null means open-ended — no fundraising target. */
    @Column(name = "goal_amount", precision = 14, scale = 2)
    private BigDecimal goalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Column(name = "start_date")
    private LocalDate startDate;

    /** Null means the campaign runs indefinitely. */
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    /** When true, unauthenticated guests can donate to this campaign. */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @OneToMany(mappedBy = "campaign")
    @Builder.Default
    private List<Donation> donations = new ArrayList<>();
}
