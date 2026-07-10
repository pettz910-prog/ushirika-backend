package com.mdau.ushirika.module.reinstatement.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.reinstatement.enums.ReinstatementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "reinstatement_requests",
    indexes = {
        @Index(name = "idx_reinstate_user",        columnList = "user_id"),
        @Index(name = "idx_reinstate_status",      columnList = "status"),
        @Index(name = "idx_reinstate_user_status", columnList = "user_id, status"),
        @Index(name = "idx_reinstate_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReinstatementRequest extends BaseEntity {

    /** Raw UUID — avoids eagerly loading the User entity. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private ReinstatementStatus status = ReinstatementStatus.PENDING;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    /** UUID of the admin who reviewed the request. */
    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
