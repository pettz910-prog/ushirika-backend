package com.mdau.ushirika.module.event.dto;

import com.mdau.ushirika.module.event.enums.EventType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventRequest(

        @NotBlank(message = "Event title is required")
        String title,

        String description,

        @NotNull(message = "Event type is required")
        EventType type,

        String venue,

        String onlineLink,

        @NotNull(message = "Start date and time is required")
        @Future(message = "Start date must be in the future")
        LocalDateTime startDateTime,

        LocalDateTime endDateTime,

        LocalDateTime registrationDeadline,

        /** Null means unlimited capacity. */
        @Positive(message = "Capacity must be a positive number if specified")
        Integer capacity,

        boolean membersOnly,

        /** True = registration requires a payment reference before it is confirmed. */
        boolean requiresPayment,

        /** Ticket price in USD. Required when requiresPayment is true. */
        @PositiveOrZero(message = "Ticket price must be zero or positive")
        BigDecimal ticketPrice,

        String coverImageUrl,

        List<String> tags
) {}
