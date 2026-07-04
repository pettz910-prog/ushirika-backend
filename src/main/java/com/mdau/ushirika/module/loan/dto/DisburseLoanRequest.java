package com.mdau.ushirika.module.loan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DisburseLoanRequest(
        @NotBlank @Size(max = 100) String disbursementMethod,
        @Size(max = 100) String disbursementReference,
        @Size(max = 1000) String notes
) {}
