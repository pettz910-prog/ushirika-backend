package com.mdau.ushirika.module.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AdminEditForumPostRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        @NotBlank(message = "Body is required")
        String body,

        @Size(max = 5)
        List<String> mediaUrls,

        @Size(max = 500)
        String videoUrl,

        String adminNotes,

        boolean featured
) {}
