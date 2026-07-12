package com.mdau.ushirika.module.attendance.dto;

import com.mdau.ushirika.module.payment.enums.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SubmitFinePaymentRequest(

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Payment mode is required")
        PaymentMode paymentMode,

        @NotBlank(message = "Transaction reference is required")
        String memberTxReference,

        String notes
) {}
