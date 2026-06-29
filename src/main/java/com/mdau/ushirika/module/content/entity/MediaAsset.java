package com.mdau.ushirika.module.content.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "media_assets",
    indexes = {
        @Index(name = "idx_ma_public_id",    columnList = "public_id"),
        @Index(name = "idx_ma_uploaded_by",  columnList = "uploaded_by_id"),
        @Index(name = "idx_ma_folder",       columnList = "folder"),
        @Index(name = "idx_ma_created_at",   columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset extends BaseEntity {

    /** Cloudinary public_id — used for deletion and transformation URLs. */
    @Column(name = "public_id", nullable = false, unique = true, length = 200)
    private String publicId;

    /** Full Cloudinary delivery URL (already signed/CDN). */
    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    /** Logical grouping folder in Cloudinary (e.g. "articles", "events"). */
    @Column(name = "folder", length = 100)
    private String folder;

    @Column(name = "original_filename", length = 200)
    private String originalFilename;

    /** File extension/format: jpg, png, webp, pdf, etc. */
    @Column(name = "format", length = 10)
    private String format;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id",
                foreignKey = @ForeignKey(name = "fk_ma_uploaded_by"))
    private User uploadedBy;
}
