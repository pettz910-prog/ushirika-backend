package com.mdau.ushirika.module.election.dto;

import com.mdau.ushirika.module.election.entity.Election;
import com.mdau.ushirika.module.election.enums.ElectionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ElectionSummaryDto(
        UUID id,
        String title,
        int year,
        LocalDate nominationsStart,
        LocalDate nominationsEnd,
        LocalDateTime votingStart,
        LocalDateTime votingEnd,
        ElectionStatus status,
        String videoUrl,
        int seatCount
) {
    public static ElectionSummaryDto from(Election e) {
        return new ElectionSummaryDto(
                e.getId(),
                e.getTitle(),
                e.getYear(),
                e.getNominationsStart(),
                e.getNominationsEnd(),
                e.getVotingStart(),
                e.getVotingEnd(),
                e.getStatus(),
                e.getVideoUrl(),
                e.getSeats().size()
        );
    }
}
