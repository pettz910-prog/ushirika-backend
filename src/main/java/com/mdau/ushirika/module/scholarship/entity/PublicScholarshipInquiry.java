package com.mdau.ushirika.module.scholarship.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Unauthenticated public inquiry — low priority feature.
 * Stored for admin follow-up but has no approval flow.
 */
@Entity
@Table(
    name = "public_scholarship_inquiries",
    indexes = {
        @Index(name = "idx_psi_program_id",  columnList = "program_id"),
        @Index(name = "idx_psi_created_at",  columnList = "created_at"),
        @Index(name = "idx_psi_email",       columnList = "email")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicScholarshipInquiry extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /** The program they are inquiring about — nullable (general inquiry). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id",
                foreignKey = @ForeignKey(name = "fk_psi_program"))
    private ScholarshipProgram program;
}
