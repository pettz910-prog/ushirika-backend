package com.mdau.ushirika.module.calendar.dto;

import com.mdau.ushirika.module.calendar.enums.CalendarItemType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarItemDto(
        CalendarItemType type,
        UUID id,
        String title,
        String description,
        LocalDateTime start,
        LocalDateTime end,
        String location,
        String actionUrl
) {}
