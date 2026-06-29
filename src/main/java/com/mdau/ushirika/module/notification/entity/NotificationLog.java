package com.mdau.ushirika.module.notification.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.notification.enums.NotificationChannel;
import com.mdau.ushirika.module.notification.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "notification_logs",
    indexes = {
        @Index(name = "idx_nl_channel",         columnList = "channel"),
        @Index(name = "idx_nl_status",          columnList = "status"),
        @Index(name = "idx_nl_recipient",       columnList = "recipient"),
        @Index(name = "idx_nl_channel_status",  columnList = "channel, status"),
        @Index(name = "idx_nl_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 10)
    private NotificationChannel channel;

    /** Email address or phone number. */
    @Column(name = "recipient", nullable = false, length = 150)
    private String recipient;

    @Column(name = "recipient_name", length = 150)
    private String recipientName;

    /** Subject line for emails; null for SMS. */
    @Column(name = "subject", length = 300)
    private String subject;

    /** Truncated body — stored for audit, not for delivery replay. */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "error_message", length = 500)
    private String errorMessage;
}
