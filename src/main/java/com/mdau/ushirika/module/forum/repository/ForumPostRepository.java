package com.mdau.ushirika.module.forum.repository;

import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.forum.entity.ForumPost;
import com.mdau.ushirika.module.forum.enums.ForumPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ForumPostRepository extends JpaRepository<ForumPost, UUID> {

    Page<ForumPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ForumPost> findAllByStatusOrderByCreatedAtDesc(ForumPostStatus status, Pageable pageable);

    List<ForumPost> findAllByMemberOrderByCreatedAtDesc(User member);

    Page<ForumPost> findAllByStatusOrderByFeaturedDescApprovedAtDesc(
            ForumPostStatus status, Pageable pageable);
}
