package com.mdau.ushirika.module.notification.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.common.response.PagedResponse;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.notification.dto.InAppNotificationDto;
import com.mdau.ushirika.module.notification.service.InAppNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Notifications — Member", description = "In-app notification inbox")
public class MemberNotificationController {

    private final InAppNotificationService notificationService;

    @GetMapping("/my")
    @Operation(summary = "Get my notifications, newest first")
    public ResponseEntity<ApiResponse<PagedResponse<InAppNotificationDto>>> myNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(
                notificationService.getMyNotifications(user.getId(), page, size)));
    }

    @GetMapping("/my/unread-count")
    @Operation(summary = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        notificationService.markRead(id, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Marked as read"));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all my notifications as read")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllRead(
            @AuthenticationPrincipal User user) {
        int count = notificationService.markAllRead(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("All marked as read", Map.of("updated", count)));
    }
}
