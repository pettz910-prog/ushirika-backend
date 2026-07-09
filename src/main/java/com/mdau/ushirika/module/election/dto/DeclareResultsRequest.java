package com.mdau.ushirika.module.election.dto;

import java.util.List;
import java.util.UUID;

public record DeclareResultsRequest(
        List<TieBreakerEntry> tieBreakers
) {
    public record TieBreakerEntry(
            UUID candidacyId,
            boolean winner,
            String notes
    ) {}
}
