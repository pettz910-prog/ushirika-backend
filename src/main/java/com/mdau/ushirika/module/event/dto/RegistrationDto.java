package com.mdau.ushirika.module.event.dto;

import com.mdau.ushirika.module.event.entity.EventRegistration;
import com.mdau.ushirika.module.event.enums.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistrationDto(
        UUID id,
        UUID eventId,
        String eventTitle,
        String referenceCode,
        String displayName,
        String displayEmail,
        boolean memberRegistration,
        RegistrationStatus status,
        LocalDateTime registeredAt,
        LocalDateTime attendanceMarkedAt
) {
    public static RegistrationDto from(EventRegistration r) {
        return new RegistrationDto(
                r.getId(),
                r.getEvent().getId(),
                r.getEvent().getTitle(),
                r.getReferenceCode(),
                r.getDisplayName(),
                r.getDisplayEmail(),
                r.isMemberRegistration(),
                r.getStatus(),
                r.getRegisteredAt(),
                r.getAttendanceMarkedAt()
        );
    }
}
