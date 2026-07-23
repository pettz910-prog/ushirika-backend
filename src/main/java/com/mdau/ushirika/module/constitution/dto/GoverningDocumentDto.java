package com.mdau.ushirika.module.constitution.dto;

import com.mdau.ushirika.module.constitution.entity.GoverningDocument;
import com.mdau.ushirika.module.constitution.enums.DocumentStatus;
import com.mdau.ushirika.module.constitution.enums.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GoverningDocumentDto(
        UUID           id,
        String         title,
        DocumentType   documentType,
        String         description,
        String         documentVersion,
        String         fileUrl,
        String         contentText,
        LocalDate      effectiveDate,
        DocumentStatus status,
        LocalDateTime  publishedAt,
        int            sortOrder,
        LocalDateTime  createdAt,
        LocalDateTime  updatedAt
) {
    public static GoverningDocumentDto from(GoverningDocument d) {
        return new GoverningDocumentDto(
                d.getId(), d.getTitle(), d.getDocumentType(),
                d.getDescription(), d.getDocumentVersion(), d.getFileUrl(), d.getContentText(),
                d.getEffectiveDate(), d.getStatus(), d.getPublishedAt(),
                d.getSortOrder(), d.getCreatedAt(), d.getUpdatedAt()
        );
    }
}
