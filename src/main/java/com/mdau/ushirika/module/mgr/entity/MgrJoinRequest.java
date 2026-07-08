package com.mdau.ushirika.module.mgr.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.mgr.enums.JoinRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "mgr_join_requests",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_mgr_jr_cycle_user",
        columnNames = {"cycle_id", "user_id"}
    ),
    indexes = {
        @Index(name = "idx_mgr_jr_cycle",  columnList = "cycle_id"),
        @Index(name = "idx_mgr_jr_user",   columnList = "user_id"),
        @Index(name = "idx_mgr_jr_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MgrJoinRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_jr_cycle"))
    private MgrCycle cycle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_mgr_jr_user"))
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private JoinRequestStatus status = JoinRequestStatus.PENDING;

    @Column(name = "member_notes", length = 500)
    private String memberNotes;

    @Column(name = "admin_notes", length = 500)
    private String adminNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by_id",
                foreignKey = @ForeignKey(name = "fk_mgr_jr_responded_by"))
    private User respondedBy;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}
