package com.mdau.ushirika.module.report.dto;

import com.mdau.ushirika.module.attendance.enums.MeetingType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceMeetingHeader(
        UUID id,
        String title,
        LocalDateTime meetingDate,
        MeetingType type
) {}
