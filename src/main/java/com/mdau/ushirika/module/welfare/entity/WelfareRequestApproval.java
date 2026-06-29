package com.mdau.ushirika.module.welfare.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.ApprovalDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "welfare_request_approvals",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_wra_request_admin",
        columnNames = {"welfare_request_id", "admin_id"}
    ),
    indexes = {
        @Index(name = "idx_wra_request_decision", columnList = "welfare_request_id, decision"),
        @Index(name = "idx_wra_decided_at",       columnList = "decided_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelfareRequestApproval extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "welfare_request_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_wra_request"))
    private WelfareRequest welfareRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_wra_admin"))
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 10)
    private ApprovalDecision decision;

    /** Internal only — never exposed to the applicant. */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;
}
