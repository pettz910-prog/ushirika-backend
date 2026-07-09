package com.mdau.ushirika.module.election.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateElectionRequest(
        String title,
        LocalDate nominationsStart,
        LocalDate nominationsEnd,
        LocalDateTime votingStart,
        LocalDateTime votingEnd,
        String notes
) {}
