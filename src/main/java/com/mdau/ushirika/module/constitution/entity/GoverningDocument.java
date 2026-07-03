package com.mdau.ushirika.module.constitution.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.constitution.enums.DocumentStatus;
import com.mdau.ushirika.module.constitution.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "governing_documents",
    indexes = {
        @Index(name = "idx_gdoc_status",      columnList = "status"),
        @Index(name = "idx_gdoc_type",        columnList = "document_type"),
        @Index(name = "idx_gdoc_status_type", columnList = "status, document_type"),
        @Index(name = "idx_gdoc_sort_order",  columnList = "sort_order"),
        @Index(name = "idx_gdoc_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoverningDocument extends BaseEntity {

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @Column(name = "description", length = 1000)
    private String description;

    /** Version label shown to members — e.g. "2024 Edition", "v3.1". */
    @Column(name = "document_version", length = 50)
    private String documentVersion;

    /** Cloudinary or external URL to the PDF/document file. */
    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    /** Cloudinary public_id — used for deletion. Null for external links. */
    @Column(name = "file_public_id", length = 200)
    private String filePublicId;

    /** When this document came into effect. */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /** Controls display ordering on the public page — lower = first. */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
