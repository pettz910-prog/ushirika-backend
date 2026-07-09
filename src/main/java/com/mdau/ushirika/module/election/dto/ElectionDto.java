package com.mdau.ushirika.module.election.dto;

import com.mdau.ushirika.module.election.entity.Election;
import com.mdau.ushirika.module.election.enums.ElectionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ElectionDto(
        UUID id,
        String title,
        int year,
        LocalDate nominationsStart,
        LocalDate nominationsEnd,
        LocalDateTime votingStart,
        LocalDateTime votingEnd,
        ElectionStatus status,
        String videoUrl,
        String notes,
        LocalDateTime resultsDeclaredAt,
        LocalDateTime completedAt,
        List<ElectionSeatDto> seats,
        List<ElectionResultDto> results
) {
    public static ElectionDto from(Election e, List<ElectionSeatDto> seats, List<ElectionResultDto> results) {
        return new ElectionDto(
                e.getId(),
                e.getTitle(),
                e.getYear(),
                e.getNominationsStart(),
                e.getNominationsEnd(),
                e.getVotingStart(),
                e.getVotingEnd(),
                e.getStatus(),
                e.getVideoUrl(),
                e.getNotes(),
                e.getResultsDeclaredAt(),
                e.getCompletedAt(),
                seats,
                results
        );
    }
}
