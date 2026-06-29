package com.mdau.ushirika.module.scholarship.dto;

import com.mdau.ushirika.module.welfare.enums.DisbursementMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ScholarshipAwardRequest(

        @NotNull(message = "Amount awarded is required")
        @DecimalMin(value = "1.00", message = "Award amount must be at least 1.00")
        BigDecimal amountAwarded,

        @NotNull(message = "Disbursement method is required")
        DisbursementMethod method,

        String transactionReference,

        String notes
) {}
