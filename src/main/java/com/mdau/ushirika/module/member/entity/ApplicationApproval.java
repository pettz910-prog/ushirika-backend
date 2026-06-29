package com.mdau.ushirika.module.member.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "application_approvals",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_approval_application_admin",
        columnNames = {"application_id", "admin_id"}
    ),
    indexes = {
        // uq constraint above auto-creates an index on (application_id, admin_id)
        // Composite for quorum count query: WHERE application_id = ? AND decision = ?
        @Index(name = "idx_aa_app_decision", columnList = "application_id, decision"),
        @Index(name = "idx_aa_decided_at",   columnList = "decided_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationApproval extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aa_application"))
    private MembershipApplication application;

    /**
     * The admin who cast this vote.
     * Exposed only to SUPERADMIN — never shown in member-facing or peer-admin views.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_aa_admin"))
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 10)
    private ApprovalDecision decision;

    /** Optional internal comment. Visible only to SUPERADMIN. */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;
}
