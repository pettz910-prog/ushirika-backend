package com.mdau.ushirika.module.welfare.dto;

import com.mdau.ushirika.module.welfare.enums.DisbursementMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DisbursementRequest(

        @NotNull(message = "Amount disbursed is required")
        @DecimalMin(value = "1.00", message = "Disbursement must be at least 1.00")
        BigDecimal amountDisbursed,

        @NotNull(message = "Disbursement method is required")
        DisbursementMethod method,

        /** Bank reference, M-Pesa code, cheque number — optional for cash. */
        String transactionReference,

        String notes
) {}
