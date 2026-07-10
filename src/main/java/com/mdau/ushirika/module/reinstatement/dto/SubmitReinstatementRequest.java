package com.mdau.ushirika.module.reinstatement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitReinstatementRequest(
        @NotBlank @Size(max = 2000) String reason
) {}
