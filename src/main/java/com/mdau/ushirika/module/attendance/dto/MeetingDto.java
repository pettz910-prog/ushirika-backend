package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.entity.Meeting;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingDto(
        UUID id,
        String title,
        String description,
        LocalDateTime meetingDate,
        String location,
        String type,
        String status,
        String notes,
        LocalDateTime createdAt
) {
    public static MeetingDto from(Meeting m) {
        return new MeetingDto(
                m.getId(), m.getTitle(), m.getDescription(),
                m.getMeetingDate(), m.getLocation(),
                m.getType().name(), m.getStatus().name(),
                m.getNotes(), m.getCreatedAt()
        );
    }
}
