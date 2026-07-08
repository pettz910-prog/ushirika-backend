package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.Size;

public record RespondJoinRequest(
        @Size(max = 500)
        String adminNotes
) {}
