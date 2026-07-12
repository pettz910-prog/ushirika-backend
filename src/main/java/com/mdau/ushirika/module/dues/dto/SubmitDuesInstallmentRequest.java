package com.mdau.ushirika.module.dues.dto;

import com.mdau.ushirika.module.payment.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmitDuesInstallmentRequest(

        @NotNull(message = "Dues record ID is required")
        UUID duesId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum installment is $1.00")
        BigDecimal amount,

        @NotNull(message = "Payment mode is required")
        PaymentMode paymentMode,

        @NotBlank(message = "Transaction reference is required")
        String memberTxReference,

        String notes
) {}
