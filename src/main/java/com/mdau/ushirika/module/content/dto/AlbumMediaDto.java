package com.mdau.ushirika.module.content.dto;

import com.mdau.ushirika.module.content.entity.AlbumMedia;

import java.util.UUID;

public record AlbumMediaDto(
        UUID    id,
        String  publicId,
        String  url,
        String  format,
        String  caption,
        int     sortOrder,
        Integer width,
        Integer height
) {
    public static AlbumMediaDto from(AlbumMedia m) {
        return new AlbumMediaDto(
                m.getId(), m.getPublicId(), m.getUrl(), m.getFormat(),
                m.getCaption(), m.getSortOrder(), m.getWidth(), m.getHeight()
        );
    }
}
