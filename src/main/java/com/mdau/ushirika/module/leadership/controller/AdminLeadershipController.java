package com.mdau.ushirika.module.leadership.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.leadership.dto.LeadershipOfficialDto;
import com.mdau.ushirika.module.leadership.dto.SaveOfficialRequest;
import com.mdau.ushirika.module.leadership.service.LeadershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/leadership")
@RequiredArgsConstructor
public class AdminLeadershipController {

    private final LeadershipService leadershipService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadershipOfficialDto>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(leadershipService.listAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeadershipOfficialDto>> create(
            @Valid @RequestBody SaveOfficialRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Official added.", leadershipService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadershipOfficialDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody SaveOfficialRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Official updated.", leadershipService.update(id, req)));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<LeadershipOfficialDto>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded.", leadershipService.uploadImage(id, file)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<LeadershipOfficialDto>> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Visibility toggled.", leadershipService.toggle(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        leadershipService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Official removed."));
    }
}
