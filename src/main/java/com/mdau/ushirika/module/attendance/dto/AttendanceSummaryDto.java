package com.mdau.ushirika.module.attendance.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttendanceSummaryDto(
        int totalMeetings,
        int attended,
        int absent,
        int excused,
        int consecutiveAbsences,
        boolean atRisk,
        boolean membershipCeased,
        List<MeetingItem> history
) {
    public record MeetingItem(
            UUID meetingId,
            String title,
            LocalDateTime meetingDate,
            String type,
            String attendanceStatus
    ) {}
}
