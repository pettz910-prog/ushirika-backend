package com.mdau.ushirika.module.notification.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.notification.enums.InAppNotificationCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "in_app_notifications",
    indexes = {
        @Index(name = "idx_ian_user",       columnList = "user_id"),
        @Index(name = "idx_ian_read",       columnList = "read"),
        @Index(name = "idx_ian_user_read",  columnList = "user_id, read"),
        @Index(name = "idx_ian_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotification extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 25)
    private InAppNotificationCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Optional deep-link path inside the portal, e.g. "/portal/meetings". */
    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
