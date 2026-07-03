package com.mdau.ushirika.module.content.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.content.enums.AlbumStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "community_albums",
    indexes = {
        @Index(name = "idx_calb_status",          columnList = "status"),
        @Index(name = "idx_calb_event_date",      columnList = "event_date"),
        @Index(name = "idx_calb_status_event",    columnList = "status, event_date"),
        @Index(name = "idx_calb_published_at",    columnList = "published_at"),
        @Index(name = "idx_calb_created_at",      columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityAlbum extends BaseEntity {

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "cover_image_url", length = 1000)
    private String coverImageUrl;

    @Column(name = "cover_image_public_id", length = 200)
    private String coverImagePublicId;

    /** Date the event/gathering took place — used for display and ordering. */
    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "location", length = 300)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    @Builder.Default
    private AlbumStatus status = AlbumStatus.DRAFT;

    /** Set when status transitions DRAFT → PUBLISHED. */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<AlbumMedia> media = new ArrayList<>();
}
