package com.mdau.ushirika.module.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPeerPaymentRequest(

    @NotBlank(message = "Please enter the transaction reference from your payment app.")
    @Size(min = 4, max = 100, message = "Transaction reference must be between 4 and 100 characters.")
    String adminTxReference
) {}
