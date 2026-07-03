package com.mdau.ushirika.module.content.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "album_media",
    indexes = {
        @Index(name = "idx_albm_album_id",    columnList = "album_id"),
        @Index(name = "idx_albm_sort_order",  columnList = "album_id, sort_order"),
        @Index(name = "idx_albm_public_id",   columnList = "public_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumMedia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_albm_album"))
    private CommunityAlbum album;

    @Column(name = "public_id", nullable = false, length = 200)
    private String publicId;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "format", length = 10)
    private String format;

    @Column(name = "caption", length = 500)
    private String caption;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;
}
