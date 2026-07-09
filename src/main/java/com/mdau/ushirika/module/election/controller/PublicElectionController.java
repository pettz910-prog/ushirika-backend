package com.mdau.ushirika.module.election.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.election.dto.ElectionDto;
import com.mdau.ushirika.module.election.dto.ElectionSummaryDto;
import com.mdau.ushirika.module.election.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/public/elections")
@RequiredArgsConstructor
public class PublicElectionController {

    private final ElectionService electionService;

    /** Returns the current active election (nominations/voting/closed) or empty. */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ElectionDto>> getActive() {
        Optional<ElectionDto> dto = electionService.getActivePublic();
        return ResponseEntity.ok(ApiResponse.ok(dto.orElse(null)));
    }

    /** Lists all elections whose results have been published. */
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<List<ElectionSummaryDto>>> listResults() {
        return ResponseEntity.ok(ApiResponse.ok(electionService.listPublishedResults()));
    }

    /** Full results for a specific past election. */
    @GetMapping("/{id}/results")
    public ResponseEntity<ApiResponse<ElectionDto>> getResults(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(electionService.getPublicResults(id)));
    }
}
