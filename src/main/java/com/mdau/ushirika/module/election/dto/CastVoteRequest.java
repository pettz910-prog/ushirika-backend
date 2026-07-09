package com.mdau.ushirika.module.election.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CastVoteRequest(
        @NotNull UUID seatId,
        @NotNull UUID candidacyId
) {}
