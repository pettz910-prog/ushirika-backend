package com.mdau.ushirika.module.event.dto;

import com.mdau.ushirika.module.event.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotNull(message = "Status is required")
        RegistrationStatus status,

        /** Admin may pass a referenceCode to look up a registration directly. */
        String referenceCode
) {}
