package com.mdau.ushirika.module.election.dto;

import com.mdau.ushirika.module.election.entity.ElectionResult;

import java.util.UUID;

public record ElectionResultDto(
        UUID id,
        UUID seatId,
        String seatTitle,
        UUID candidacyId,
        String memberName,
        String memberId,
        long voteCount,
        int rank,
        boolean winner,
        boolean tieBroken,
        String notes
) {
    public static ElectionResultDto from(ElectionResult r) {
        return new ElectionResultDto(
                r.getId(),
                r.getSeat().getId(),
                r.getSeatTitle(),
                r.getCandidacy().getId(),
                r.getMemberName(),
                r.getMemberId(),
                r.getVoteCount(),
                r.getRank(),
                r.isWinner(),
                r.isTieBroken(),
                r.getNotes()
        );
    }
}
