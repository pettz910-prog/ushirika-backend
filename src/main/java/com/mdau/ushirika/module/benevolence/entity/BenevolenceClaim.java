package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.benevolence.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "benevolence_claims",
    indexes = {
        @Index(name = "idx_ben_claim_enrollment", columnList = "enrollment_id"),
        @Index(name = "idx_ben_claim_status",     columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenevolenceClaim extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_claim_enrollment"))
    private BenevolenceEnrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id",
                foreignKey = @ForeignKey(name = "fk_claim_beneficiary"))
    private BenevolenceBeneficiary beneficiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
                foreignKey = @ForeignKey(name = "fk_claim_category"))
    private BenevolenceClaimCategory category;

    @Column(name = "reference_number", unique = true, nullable = false, updatable = false, length = 30)
    private String referenceNumber;

    @Column(name = "deceased_name", nullable = false, length = 200)
    private String deceasedName;

    @Column(nullable = false, length = 50)
    private String relationship;

    @Column(name = "date_of_death", nullable = false)
    private LocalDate dateOfDeath;

    @Column(name = "location_of_death", nullable = false, length = 300)
    private String locationOfDeath;

    @Column(name = "funeral_date")
    private LocalDate funeralDate;

    @Column(name = "funeral_location", length = 300)
    private String funeralLocation;

    @Column(name = "contact_name", nullable = false, length = 200)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 30)
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document_urls", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> documentUrls = new ArrayList<>();

    @Column(name = "amount_approved", precision = 10, scale = 2)
    private BigDecimal amountApproved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "disbursed_at")
    private LocalDateTime disbursedAt;
}
