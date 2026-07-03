package com.mdau.ushirika.module.attendance.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.attendance.enums.FineStatus;
import com.mdau.ushirika.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "fines",
    indexes = {
        @Index(name = "idx_fines_user",     columnList = "user_id"),
        @Index(name = "idx_fines_status",   columnList = "status"),
        @Index(name = "idx_fines_due_date", columnList = "due_date")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_fine_user"))
    private User user;

    /** Nullable — fines can be standalone or tied to a specific meeting. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id",
                foreignKey = @ForeignKey(name = "fk_fine_meeting"))
    private Meeting meeting;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FineStatus status = FineStatus.PENDING;

    @Column(name = "waived_reason", length = 500)
    private String waivedReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
