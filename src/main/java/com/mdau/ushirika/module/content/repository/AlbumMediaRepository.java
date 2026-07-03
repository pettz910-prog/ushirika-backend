package com.mdau.ushirika.module.content.repository;

import com.mdau.ushirika.module.content.entity.AlbumMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AlbumMediaRepository extends JpaRepository<AlbumMedia, UUID> {

    /** Max sort order within an album — used to append new items at the end. */
    @Query("SELECT COALESCE(MAX(m.sortOrder), -1) FROM AlbumMedia m WHERE m.album.id = :albumId")
    int findMaxSortOrderByAlbumId(UUID albumId);
}
