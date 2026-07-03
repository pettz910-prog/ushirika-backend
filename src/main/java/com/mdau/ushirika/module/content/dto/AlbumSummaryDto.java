package com.mdau.ushirika.module.content.dto;

import com.mdau.ushirika.module.content.entity.CommunityAlbum;
import com.mdau.ushirika.module.content.enums.AlbumStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AlbumSummaryDto(
        UUID            id,
        String          title,
        String          description,
        String          coverImageUrl,
        LocalDate       eventDate,
        String          location,
        AlbumStatus     status,
        LocalDateTime   publishedAt,
        int             mediaCount,
        LocalDateTime   createdAt,
        LocalDateTime   updatedAt
) {
    public static AlbumSummaryDto from(CommunityAlbum a) {
        return new AlbumSummaryDto(
                a.getId(), a.getTitle(), a.getDescription(), a.getCoverImageUrl(),
                a.getEventDate(), a.getLocation(), a.getStatus(), a.getPublishedAt(),
                a.getMedia().size(), a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
