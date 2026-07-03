package com.mdau.ushirika.module.contact.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.contact.dto.ContactMessageDto;
import com.mdau.ushirika.module.contact.enums.ContactMessageStatus;
import com.mdau.ushirika.module.contact.service.ContactMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/contact")
@RequiredArgsConstructor
public class AdminContactController {

    private final ContactMessageService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactMessageDto>>> list(
            @RequestParam(required = false) ContactMessageStatus status) {
        List<ContactMessageDto> result = status != null
                ? service.listByStatus(status)
                : service.listAll();
        return ResponseEntity.ok(ApiResponse.ok("Messages retrieved", result));
    }

    @GetMapping("/count/new")
    public ResponseEntity<ApiResponse<Long>> countNew() {
        return ResponseEntity.ok(ApiResponse.ok("Count retrieved", service.countNew()));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<ContactMessageDto>> markRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Marked as read", service.markRead(id)));
    }

    @PostMapping("/{id}/replied")
    public ResponseEntity<ApiResponse<ContactMessageDto>> markReplied(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Marked as replied", service.markReplied(id)));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ContactMessageDto>> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Archived", service.archive(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
