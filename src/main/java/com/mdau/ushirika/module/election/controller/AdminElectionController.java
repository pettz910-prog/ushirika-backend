package com.mdau.ushirika.module.election.controller;

import com.mdau.ushirika.common.response.ApiResponse;
import com.mdau.ushirika.module.election.dto.*;
import com.mdau.ushirika.module.election.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/elections")
@RequiredArgsConstructor
public class AdminElectionController {

    private final ElectionService electionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ElectionSummaryDto>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(electionService.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ElectionDto>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(electionService.getAdmin(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ElectionDto>> create(@Valid @RequestBody CreateElectionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Election created.", electionService.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ElectionDto>> update(
            @PathVariable UUID id,
            @RequestBody UpdateElectionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Election updated.", electionService.update(id, req)));
    }

    /** Advance the election to the next lifecycle stage. */
    @PostMapping("/{id}/advance")
    public ResponseEntity<ApiResponse<ElectionDto>> advance(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Status advanced.", electionService.advanceStatus(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ElectionDto>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Election cancelled.", electionService.cancelElection(id)));
    }

    /** View all candidacies with live tally counts (shown only when appropriate). */
    @GetMapping("/{id}/candidacies")
    public ResponseEntity<ApiResponse<List<CandidacyDto>>> candidacies(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(electionService.listCandidacies(id)));
    }

    @PatchMapping("/candidacies/{candidacyId}/review")
    public ResponseEntity<ApiResponse<CandidacyDto>> review(
            @PathVariable UUID candidacyId,
            @Valid @RequestBody ReviewCandidacyRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Candidacy reviewed.", electionService.reviewCandidacy(candidacyId, req)));
    }

    /** Declare winners after voting closes. Optionally supply tie-breaker overrides. */
    @PostMapping("/{id}/declare-results")
    public ResponseEntity<ApiResponse<ElectionDto>> declareResults(
            @PathVariable UUID id,
            @RequestBody(required = false) DeclareResultsRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Results declared and published.", electionService.declareResults(id, req)));
    }

    /** Finalize and archive the election after handover is complete. */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<ElectionDto>> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Election completed.", electionService.completeElection(id)));
    }

    /** Live tallies — only accessible once VOTING_CLOSED. */
    @GetMapping("/{id}/tallies")
    public ResponseEntity<ApiResponse<Map<UUID, Long>>> tallies(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(electionService.getLiveTallies(id)));
    }
}
