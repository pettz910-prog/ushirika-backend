package com.mdau.ushirika.module.content.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.content.enums.ArticleStatus;
import com.mdau.ushirika.module.content.enums.ArticleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Content blocks are stored as a flexible JSONB array.
 * Each block is a Map with at minimum a "type" key.
 * Example block shapes:
 *   { "type": "PARAGRAPH",  "text": "..." }
 *   { "type": "HEADING",    "text": "...", "level": 2 }
 *   { "type": "IMAGE",      "url": "...", "caption": "...", "publicId": "..." }
 *   { "type": "LIST",       "items": ["...", "..."], "ordered": false }
 *   { "type": "QUOTE",      "text": "...", "author": "..." }
 *   { "type": "DIVIDER" }
 */
@Entity
@Table(
    name = "articles",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_article_slug", columnNames = "slug"
    ),
    indexes = {
        @Index(name = "idx_art_status",          columnList = "status"),
        @Index(name = "idx_art_type",            columnList = "type"),
        @Index(name = "idx_art_slug",            columnList = "slug"),
        @Index(name = "idx_art_published_at",    columnList = "published_at"),
        // Primary public listing query
        @Index(name = "idx_art_status_pub",      columnList = "status, published_at"),
        // Filtered listing by type for published articles
        @Index(name = "idx_art_status_type",     columnList = "status, type"),
        @Index(name = "idx_art_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article extends BaseEntity {

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    /** URL-safe unique identifier — used in public URLs. */
    @Column(name = "slug", nullable = false, unique = true, length = 320)
    private String slug;

    /** Short teaser shown in article listing cards. */
    @Column(name = "excerpt", length = 500)
    private String excerpt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ArticleType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    /** Cloudinary public_id for the cover — needed for deletion. */
    @Column(name = "cover_image_public_id", length = 200)
    private String coverImagePublicId;

    /**
     * Flexible JSONB content blocks — array of heterogeneous block objects.
     * Schema is enforced by the frontend editor, not the backend.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", columnDefinition = "jsonb")
    @Builder.Default
    private List<Map<String, Object>> content = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /** Set when status transitions DRAFT → PUBLISHED. */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
