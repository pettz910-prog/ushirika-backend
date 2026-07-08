package com.mdau.ushirika.module.mgr.dto;

import jakarta.validation.constraints.Size;

public record ConfirmReceiptRequest(
        @Size(max = 500)
        String notes
) {}
