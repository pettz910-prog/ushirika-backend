package com.mdau.ushirika.module.report.dto;

import java.util.List;

public record AttendanceComplianceReport(
        List<AttendanceMeetingHeader> meetings,
        List<AttendanceComplianceRow> rows,
        int totalMembers,
        int totalMeetings
) {}
