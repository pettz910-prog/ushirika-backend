package com.mdau.ushirika.module.scholarship.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.scholarship.enums.ScholarshipApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "scholarship_applications",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_sa_member_program",
        columnNames = {"member_id", "program_id"}
    ),
    indexes = {
        @Index(name = "idx_sa_member_id",      columnList = "member_id"),
        @Index(name = "idx_sa_program_id",     columnList = "program_id"),
        @Index(name = "idx_sa_status",         columnList = "status"),
        @Index(name = "idx_sa_member_status",  columnList = "member_id, status"),
        @Index(name = "idx_sa_submitted_at",   columnList = "submitted_at"),
        @Index(name = "idx_sa_created_at",     columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipApplication extends BaseEntity {

    /** The member applying — must be an approved member with a memberId. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sa_member"))
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sa_program"))
    private ScholarshipProgram program;

    @Column(name = "reference_number", unique = true, nullable = false,
            updatable = false, length = 30)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ScholarshipApplicationStatus status = ScholarshipApplicationStatus.DRAFT;

    /** Name of the person who will receive the scholarship (may be applicant's child). */
    @Column(name = "beneficiary_name", nullable = false, length = 150)
    private String beneficiaryName;

    @Column(name = "institution_name", nullable = false, length = 200)
    private String institutionName;

    @Column(name = "course_of_study", nullable = false, length = 200)
    private String courseOfStudy;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    /** Supporting documents: admission letter, fee structure, academic transcripts. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "document_urls", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> documentUrls = new ArrayList<>();

    @Column(name = "personal_statement", columnDefinition = "TEXT")
    private String personalStatement;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScholarshipApproval> approvals = new ArrayList<>();

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private ScholarshipAward award;
}
