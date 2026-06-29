package com.mdau.ushirika.module.manualpayment.dto;

import jakarta.validation.constraints.Size;

public record ReviewManualPaymentRequest(

    /** Required on rejection. Optional context note on approval. */
    @Size(max = 2000)
    String reason
) {}
