package com.mdau.ushirika.module.notification.dto;

import com.mdau.ushirika.module.notification.enums.InAppNotificationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BroadcastRequest(

        @NotNull
        InAppNotificationCategory category,

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        String body,

        /** Portal route to link to, e.g. "/portal/meetings". Optional. */
        @Size(max = 500)
        String actionUrl,

        /**
         * Delivery channels in addition to in-app.
         * Accepted values: "EMAIL", "SMS". Empty or null = in-app only.
         */
        Set<String> channels
) {}
