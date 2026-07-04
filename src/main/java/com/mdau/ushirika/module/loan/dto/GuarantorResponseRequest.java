package com.mdau.ushirika.module.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuarantorResponseRequest(
        @NotBlank String decision,   // ACCEPTED | DECLINED
        @Size(max = 500) String notes
) {}
