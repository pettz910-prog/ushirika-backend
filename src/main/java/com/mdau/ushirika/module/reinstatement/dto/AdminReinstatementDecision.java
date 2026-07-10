package com.mdau.ushirika.module.reinstatement.dto;

import jakarta.validation.constraints.Size;

public record AdminReinstatementDecision(
        @Size(max = 2000) String adminNotes
) {}
