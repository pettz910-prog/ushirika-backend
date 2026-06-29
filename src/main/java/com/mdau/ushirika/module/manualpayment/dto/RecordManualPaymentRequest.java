package com.mdau.ushirika.module.manualpayment.dto;

import com.mdau.ushirika.module.manualpayment.enums.ManualPaymentCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecordManualPaymentRequest(

    @NotNull(message = "Category is required")
    ManualPaymentCategory category,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    BigDecimal amount,

    /** Link to a registered member. Required when category = CONTRIBUTION. */
    UUID memberId,

    /** Required when memberId is null — identifies the walk-in payer. */
    @Size(max = 200)
    String payerName,

    @Email
    @Size(max = 150)
    String payerEmail,

    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,

    @NotBlank(message = "Receipt number is required")
    @Size(max = 100)
    String receiptNumber,

    /** Required when category = CONTRIBUTION — e.g. "2025-06". */
    @Size(max = 10)
    String period,

    @Size(max = 2000)
    String notes
) {}
