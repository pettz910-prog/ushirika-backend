package com.mdau.ushirika.module.scholarship.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "scholarship_approvals",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_sch_approval_app_admin",
        columnNames = {"application_id", "admin_id"}
    ),
    indexes = {
        @Index(name = "idx_sca_app_decision", columnList = "application_id, decision"),
        @Index(name = "idx_sca_decided_at",   columnList = "decided_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipApproval extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sca_application"))
    private ScholarshipApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_sca_admin"))
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 10)
    private ApprovalDecision decision;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;
}
