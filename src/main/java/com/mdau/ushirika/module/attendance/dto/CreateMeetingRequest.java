package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.enums.MeetingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMeetingRequest(
        @NotBlank(message = "Title is required") String title,
        String description,
        @NotNull(message = "Meeting date is required") LocalDateTime meetingDate,
        String location,
        @NotNull(message = "Meeting type is required") MeetingType type,
        String notes
) {}
