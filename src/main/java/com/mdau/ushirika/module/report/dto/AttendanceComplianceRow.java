package com.mdau.ushirika.module.report.dto;

import java.util.Map;
import java.util.UUID;

public record AttendanceComplianceRow(
        UUID userId,
        String memberName,
        String memberId,
        String email,
        /** meetingId → status string (PRESENT / ABSENT / EXCUSED / LATE / NOT_RECORDED) */
        Map<String, String> statuses
) {}
