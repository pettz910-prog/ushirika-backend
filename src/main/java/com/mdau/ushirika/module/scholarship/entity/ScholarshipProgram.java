package com.mdau.ushirika.module.scholarship.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipProgramStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "scholarship_programs",
    indexes = {
        @Index(name = "idx_sp_status",           columnList = "status"),
        @Index(name = "idx_sp_deadline",         columnList = "application_deadline"),
        @Index(name = "idx_sp_academic_year",    columnList = "academic_year"),
        @Index(name = "idx_sp_status_deadline",  columnList = "status, application_deadline")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipProgram extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "eligibility_criteria", columnDefinition = "TEXT")
    private String eligibilityCriteria;

    @Column(name = "amount_per_recipient", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPerRecipient;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    /**
     * Maximum number of scholarships awarded per cycle.
     * Null means unlimited slots.
     */
    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private ScholarshipProgramStatus status = ScholarshipProgramStatus.DRAFT;
}
