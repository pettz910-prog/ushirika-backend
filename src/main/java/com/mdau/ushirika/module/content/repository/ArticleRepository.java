package com.mdau.ushirika.module.content.repository;

import com.mdau.ushirika.module.content.entity.Article;
import com.mdau.ushirika.module.content.enums.ArticleStatus;
import com.mdau.ushirika.module.content.enums.ArticleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    boolean existsBySlug(String slug);

    /** Public listing — all published articles, newest first. */
    Page<Article> findAllByStatusOrderByPublishedAtDesc(ArticleStatus status, Pageable pageable);

    /** Public listing filtered by type. */
    Page<Article> findAllByStatusAndTypeOrderByPublishedAtDesc(
            ArticleStatus status, ArticleType type, Pageable pageable);

    /** Admin listing — all statuses. */
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Admin listing filtered by status. */
    Page<Article> findAllByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    long countByStatus(ArticleStatus status);
}
