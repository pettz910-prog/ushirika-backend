package com.mdau.ushirika.module.notification.repository;

import com.mdau.ushirika.module.notification.entity.InAppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InAppNotificationRepository extends JpaRepository<InAppNotification, UUID> {

    Page<InAppNotification> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE InAppNotification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.userId = :userId AND n.read = false")
    int markAllReadByUserId(@Param("userId") UUID userId);
}
