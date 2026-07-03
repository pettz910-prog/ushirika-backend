package com.mdau.ushirika.module.content.repository;

import com.mdau.ushirika.module.content.entity.CommunityAlbum;
import com.mdau.ushirika.module.content.enums.AlbumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommunityAlbumRepository extends JpaRepository<CommunityAlbum, UUID> {

    /** Public listing — published albums ordered by event date descending. */
    Page<CommunityAlbum> findAllByStatusOrderByEventDateDesc(AlbumStatus status, Pageable pageable);

    /** Admin listing — all albums ordered by creation date. */
    Page<CommunityAlbum> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Admin listing filtered by status. */
    Page<CommunityAlbum> findAllByStatusOrderByCreatedAtDesc(AlbumStatus status, Pageable pageable);
}
