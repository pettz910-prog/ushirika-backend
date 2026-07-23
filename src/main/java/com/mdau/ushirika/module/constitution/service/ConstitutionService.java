package com.mdau.ushirika.module.constitution.service;

import com.mdau.ushirika.module.constitution.dto.GoverningDocumentDto;
import com.mdau.ushirika.module.constitution.dto.GoverningDocumentRequest;
import com.mdau.ushirika.module.constitution.entity.GoverningDocument;
import com.mdau.ushirika.module.constitution.enums.DocumentStatus;
import com.mdau.ushirika.module.constitution.repository.GoverningDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstitutionService {

    private final GoverningDocumentRepository repo;

    public List<GoverningDocumentDto> listPublished() {
        return repo.findAllByStatusOrderBySortOrderAscCreatedAtDesc(DocumentStatus.PUBLISHED)
                   .stream().map(GoverningDocumentDto::from).toList();
    }

    public List<GoverningDocumentDto> listAll() {
        return repo.findAllByOrderBySortOrderAscCreatedAtDesc()
                   .stream().map(GoverningDocumentDto::from).toList();
    }

    @Transactional
    public GoverningDocumentDto create(GoverningDocumentRequest req) {
        GoverningDocument doc = GoverningDocument.builder()
                .title(req.title())
                .documentType(req.documentType())
                .description(req.description())
                .documentVersion(req.documentVersion())
                .fileUrl(req.fileUrl())
                .filePublicId(req.filePublicId())
                .contentText(req.contentText())
                .effectiveDate(req.effectiveDate())
                .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                .build();
        return GoverningDocumentDto.from(repo.save(doc));
    }

    @Transactional
    public GoverningDocumentDto update(UUID id, GoverningDocumentRequest req) {
        GoverningDocument doc = findOrThrow(id);
        doc.setTitle(req.title());
        doc.setDocumentType(req.documentType());
        doc.setDescription(req.description());
        if (req.documentVersion() != null) doc.setDocumentVersion(req.documentVersion());
        if (req.fileUrl() != null) doc.setFileUrl(req.fileUrl());
        if (req.filePublicId() != null) doc.setFilePublicId(req.filePublicId());
        doc.setContentText(req.contentText());
        doc.setEffectiveDate(req.effectiveDate());
        if (req.sortOrder() != null) doc.setSortOrder(req.sortOrder());
        return GoverningDocumentDto.from(repo.save(doc));
    }

    @Transactional
    public GoverningDocumentDto publish(UUID id) {
        GoverningDocument doc = findOrThrow(id);
        doc.setStatus(DocumentStatus.PUBLISHED);
        if (doc.getPublishedAt() == null) doc.setPublishedAt(LocalDateTime.now());
        return GoverningDocumentDto.from(repo.save(doc));
    }

    @Transactional
    public GoverningDocumentDto unpublish(UUID id) {
        GoverningDocument doc = findOrThrow(id);
        doc.setStatus(DocumentStatus.DRAFT);
        return GoverningDocumentDto.from(repo.save(doc));
    }

    @Transactional
    public void delete(UUID id) {
        GoverningDocument doc = findOrThrow(id);
        repo.delete(doc);
    }

    private GoverningDocument findOrThrow(UUID id) {
        return repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    }
}
