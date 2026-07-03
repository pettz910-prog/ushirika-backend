package com.mdau.ushirika.module.constitution.dto;

import com.mdau.ushirika.module.constitution.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GoverningDocumentRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 300)
        String title,

        @NotNull(message = "Document type is required")
        DocumentType documentType,

        @Size(max = 1000)
        String description,

        @Size(max = 50)
        String documentVersion,

        @Size(max = 1000)
        String fileUrl,

        @Size(max = 200)
        String filePublicId,

        LocalDate effectiveDate,

        Integer sortOrder
) {}
