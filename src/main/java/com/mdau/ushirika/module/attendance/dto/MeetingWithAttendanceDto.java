package com.mdau.ushirika.module.attendance.dto;

import java.util.List;

public record MeetingWithAttendanceDto(
        MeetingDto meeting,
        List<AttendanceRecordDto> records,
        List<UnrecordedMemberDto> unrecorded
) {}
