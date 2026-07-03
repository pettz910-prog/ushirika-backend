package com.mdau.ushirika.module.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CommunityAlbumRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 300, message = "Title must not exceed 300 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        String coverImageUrl,
        String coverImagePublicId,

        LocalDate eventDate,

        @Size(max = 300, message = "Location must not exceed 300 characters")
        String location
) {}
