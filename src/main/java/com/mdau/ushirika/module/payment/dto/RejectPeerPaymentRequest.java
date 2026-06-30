package com.mdau.ushirika.module.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectPeerPaymentRequest(

    @NotBlank(message = "A rejection reason is required.")
    @Size(max = 500)
    String reason
) {}
