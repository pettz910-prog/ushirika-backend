package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.attendance.entity.Fine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FineDto(
        UUID id,
        UUID userId,
        String memberName,
        String email,
        String memberId,
        UUID meetingId,
        String meetingTitle,
        String reason,
        BigDecimal amount,
        LocalDate dueDate,
        String status,
        String waivedReason,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static FineDto from(Fine f, String memberId) {
        return new FineDto(
                f.getId(),
                f.getUser().getId(),
                f.getUser().getFullName(),
                f.getUser().getEmail(),
                memberId,
                f.getMeeting() != null ? f.getMeeting().getId() : null,
                f.getMeeting() != null ? f.getMeeting().getTitle() : null,
                f.getReason(),
                f.getAmount(),
                f.getDueDate(),
                f.getStatus().name(),
                f.getWaivedReason(),
                f.getPaidAt(),
                f.getCreatedAt()
        );
    }
}
