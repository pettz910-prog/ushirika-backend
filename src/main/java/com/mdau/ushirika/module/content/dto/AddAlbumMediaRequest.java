package com.mdau.ushirika.module.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddAlbumMediaRequest(

        @NotBlank(message = "publicId is required")
        @Size(max = 200)
        String publicId,

        @NotBlank(message = "url is required")
        @Size(max = 1000)
        String url,

        @Size(max = 10)
        String format,

        @Size(max = 500)
        String caption,

        Integer sortOrder,
        Integer width,
        Integer height
) {}
