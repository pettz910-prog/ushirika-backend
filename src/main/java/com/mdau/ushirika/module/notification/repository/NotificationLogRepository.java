package com.mdau.ushirika.module.notification.repository;

import com.mdau.ushirika.module.notification.entity.NotificationLog;
import com.mdau.ushirika.module.notification.enums.NotificationChannel;
import com.mdau.ushirika.module.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    Page<NotificationLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<NotificationLog> findAllByChannelOrderByCreatedAtDesc(
            NotificationChannel channel, Pageable pageable);

    Page<NotificationLog> findAllByStatusOrderByCreatedAtDesc(
            NotificationStatus status, Pageable pageable);

    Page<NotificationLog> findAllByChannelAndStatusOrderByCreatedAtDesc(
            NotificationChannel channel, NotificationStatus status, Pageable pageable);

    Page<NotificationLog> findAllByRecipientOrderByCreatedAtDesc(
            String recipient, Pageable pageable);

    long countByStatus(NotificationStatus status);

    long countByChannel(NotificationChannel channel);

    long countByChannelAndStatusAndCreatedAtAfter(
            NotificationChannel channel, NotificationStatus status,
            java.time.LocalDateTime after);
}
