package com.mdau.ushirika.module.constitution.controller;

import com.mdau.ushirika.module.constitution.dto.GoverningDocumentDto;
import com.mdau.ushirika.module.constitution.dto.GoverningDocumentRequest;
import com.mdau.ushirika.module.constitution.service.ConstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/constitution")
@RequiredArgsConstructor
public class AdminConstitutionController {

    private final ConstitutionService service;

    @GetMapping
    public ResponseEntity<List<GoverningDocumentDto>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    public ResponseEntity<GoverningDocumentDto> create(@Valid @RequestBody GoverningDocumentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoverningDocumentDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody GoverningDocumentRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<GoverningDocumentDto> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(service.publish(id));
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<GoverningDocumentDto> unpublish(@PathVariable UUID id) {
        return ResponseEntity.ok(service.unpublish(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
