package com.mdau.ushirika.module.content.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.content.entity.MediaAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

    Optional<MediaAsset> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);

    Page<MediaAsset> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<MediaAsset> findAllByFolderOrderByCreatedAtDesc(String folder, Pageable pageable);

    Page<MediaAsset> findAllByUploadedByOrderByCreatedAtDesc(User uploadedBy, Pageable pageable);
}
