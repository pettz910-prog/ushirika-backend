package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.enums.AttendanceStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BulkAttendanceRequest(
        @NotEmpty(message = "At least one attendance entry is required")
        List<Entry> entries
) {
    public record Entry(
            @NotNull(message = "userId is required") UUID userId,
            @NotNull(message = "status is required") AttendanceStatus status,
            String notes
    ) {}
}
