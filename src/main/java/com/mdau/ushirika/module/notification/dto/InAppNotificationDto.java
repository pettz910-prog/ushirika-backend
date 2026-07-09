package com.mdau.ushirika.module.notification.dto;

import com.mdau.ushirika.module.notification.entity.InAppNotification;
import com.mdau.ushirika.module.notification.enums.InAppNotificationCategory;

import java.time.LocalDateTime;
import java.util.UUID;

public record InAppNotificationDto(
        UUID id,
        InAppNotificationCategory category,
        String title,
        String body,
        String actionUrl,
        boolean read,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static InAppNotificationDto from(InAppNotification n) {
        return new InAppNotificationDto(
                n.getId(), n.getCategory(), n.getTitle(), n.getBody(),
                n.getActionUrl(), n.isRead(), n.getReadAt(), n.getCreatedAt()
        );
    }
}
