package com.mdau.ushirika.module.event.repository;

import com.mdau.ushirika.module.event.entity.Event;
import com.mdau.ushirika.module.event.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    /** Public listing: published, open to all, upcoming first. */
    Page<Event> findAllByStatusAndMembersOnlyFalseOrderByStartDateTimeAsc(
            EventStatus status, Pageable pageable);

    /** Member listing: all published events regardless of membersOnly flag. */
    Page<Event> findAllByStatusOrderByStartDateTimeAsc(EventStatus status, Pageable pageable);

    /** Admin listing: all events all statuses. */
    Page<Event> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByStatus(EventStatus status);

    /** Count confirmed attendees for capacity enforcement. */
    @Query("""
        SELECT COUNT(r) FROM EventRegistration r
        WHERE r.event.id = :eventId
        AND r.status IN ('REGISTERED', 'ATTENDED')
        """)
    long countActiveRegistrations(@Param("eventId") UUID eventId);
}
