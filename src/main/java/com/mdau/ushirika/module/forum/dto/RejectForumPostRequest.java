package com.mdau.ushirika.module.forum.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectForumPostRequest(
        @NotBlank(message = "Please provide a reason so the member can revise their story")
        String reason
) {}
