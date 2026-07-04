package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "benevolence_beneficiaries",
    indexes = @Index(name = "idx_ben_benef_enrollment", columnList = "enrollment_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenevolenceBeneficiary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_benef_enrollment"))
    private BenevolenceEnrollment enrollment;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 50)
    private String relationship;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /** Set by admin only when a claim is submitted for this beneficiary. */
    @Column(nullable = false)
    @Builder.Default
    private boolean deceased = false;

    @Column(name = "deceased_at")
    private LocalDate deceasedAt;

    /** Admin notes on any beneficiary update (audit trail). */
    @Column(name = "admin_notes", length = 500)
    private String adminNotes;
}
