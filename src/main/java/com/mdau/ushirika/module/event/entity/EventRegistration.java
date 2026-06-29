package com.mdau.ushirika.module.event.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.event.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "event_registrations",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_er_event_user",
        columnNames = {"event_id", "user_id"}
    ),
    indexes = {
        @Index(name = "idx_er_event_id",        columnList = "event_id"),
        @Index(name = "idx_er_user_id",         columnList = "user_id"),
        @Index(name = "idx_er_status",          columnList = "status"),
        @Index(name = "idx_er_event_status",    columnList = "event_id, status"),
        // Check-in lookup by reference code
        @Index(name = "idx_er_reference_code",  columnList = "reference_code"),
        @Index(name = "idx_er_guest_email",     columnList = "guest_email"),
        @Index(name = "idx_er_registered_at",   columnList = "registered_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_er_event"))
    private Event event;

    /**
     * Null for guest (public) registrations.
     * The unique constraint on (event_id, user_id) only triggers when user_id is non-null;
     * PostgreSQL treats NULL as distinct in unique constraints.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "fk_er_user"))
    private User user;

    // ── Guest fields — only populated for non-member/public registrations ──────

    @Column(name = "guest_name", length = 150)
    private String guestName;

    @Column(name = "guest_email", length = 150)
    private String guestEmail;

    @Column(name = "guest_phone", length = 20)
    private String guestPhone;

    // ── Registration state ────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    /** Short unique code for on-site check-in scanning. */
    @Column(name = "reference_code", unique = true, nullable = false,
            updatable = false, length = 12)
    private String referenceCode;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "attendance_marked_at")
    private LocalDateTime attendanceMarkedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_marked_by_id",
                foreignKey = @ForeignKey(name = "fk_er_marked_by"))
    private User attendanceMarkedBy;

    // ── Helpers ───────────────────────────────────────────────────────────────

    public boolean isMemberRegistration() {
        return user != null;
    }

    public String getDisplayName() {
        return user != null ? user.getFullName() : guestName;
    }

    public String getDisplayEmail() {
        return user != null ? user.getEmail() : guestEmail;
    }
}
