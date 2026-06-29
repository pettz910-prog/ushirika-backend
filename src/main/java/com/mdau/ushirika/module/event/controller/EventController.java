package com.mdau.ushirika.module.event.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.event.dto.*;
import com.mdau.ushirika.module.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Events — Member/Public", description = "Event listing and registration")
public class EventController {

    private final EventService eventService;

    // ─── Public

    @GetMapping("/public/events")
    @Operation(summary = "List all published public (non-members-only) events")
    public ResponseEntity<ApiResponse<PagedResponse<EventDto>>> publicEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Events retrieved",
                eventService.listPublicEvents(
                        PageRequest.of(page, size, Sort.by("startDateTime").ascending()))));
    }

    @GetMapping("/public/events/{id}")
    @Operation(summary = "Get a single published public event")
    public ResponseEntity<ApiResponse<EventDto>> publicEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Event retrieved", eventService.getPublicEvent(id)));
    }

    @PostMapping("/public/events/{id}/register")
    @Operation(summary = "Register as a guest (no account required) for a public event")
    public ResponseEntity<ApiResponse<RegistrationDto>> guestRegister(
            @PathVariable UUID id, @Valid @RequestBody GuestRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration confirmed", eventService.registerAsGuest(id, req)));
    }

    // ─── Member

    @GetMapping("/events")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all published events visible to members (including members-only)")
    public ResponseEntity<ApiResponse<PagedResponse<EventDto>>> memberEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Events retrieved",
                eventService.listMemberEvents(
                        PageRequest.of(page, size, Sort.by("startDateTime").ascending()))));
    }

    @GetMapping("/events/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get a single published event (member view)")
    public ResponseEntity<ApiResponse<EventDto>> memberEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Event retrieved", eventService.getMemberEvent(id)));
    }

    @PostMapping("/events/{id}/register")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Register for an event as a member")
    public ResponseEntity<ApiResponse<RegistrationDto>> memberRegister(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration confirmed", eventService.register(id)));
    }

    @DeleteMapping("/events/{id}/register")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel your registration for an event")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(@PathVariable UUID id) {
        eventService.cancelRegistration(id);
        return ResponseEntity.ok(ApiResponse.ok("Registration cancelled"));
    }

    @GetMapping("/events/my-registrations")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all my event registrations")
    public ResponseEntity<ApiResponse<PagedResponse<RegistrationDto>>> myRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Registrations retrieved",
                eventService.myRegistrations(
                        PageRequest.of(page, size, Sort.by("registeredAt").descending()))));
    }
}
