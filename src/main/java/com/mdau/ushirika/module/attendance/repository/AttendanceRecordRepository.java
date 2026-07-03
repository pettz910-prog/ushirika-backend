package com.mdau.ushirika.module.attendance.repository;

import com.mdau.ushirika.module.attendance.entity.AttendanceRecord;
import com.mdau.ushirika.module.attendance.entity.Meeting;
import com.mdau.ushirika.module.attendance.enums.AttendanceStatus;
import com.mdau.ushirika.module.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findByMeeting(Meeting meeting);

    Optional<AttendanceRecord> findByMeetingAndUser(Meeting meeting, User user);

    List<AttendanceRecord> findByUserOrderByCreatedAtDesc(User user);

    long countByUserAndStatus(User user, AttendanceStatus status);

    /** Returns the IDs of users who already have a record for the given meeting. */
    default Set<UUID> recordedUserIds(Meeting meeting) {
        return findByMeeting(meeting).stream()
                .map(r -> r.getUser().getId())
                .collect(java.util.stream.Collectors.toSet());
    }
}
