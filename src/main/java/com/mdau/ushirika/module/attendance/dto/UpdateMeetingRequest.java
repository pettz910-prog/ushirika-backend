package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.enums.MeetingType;

import java.time.LocalDateTime;

public record UpdateMeetingRequest(
        String title,
        String description,
        LocalDateTime meetingDate,
        String location,
        MeetingType type,
        String notes
) {}
