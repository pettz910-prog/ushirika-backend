package com.mdau.ushirika.module.scholarship.repository;

import com.mdau.ushirika.module.scholarship.entity.PublicScholarshipInquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PublicScholarshipInquiryRepository extends JpaRepository<PublicScholarshipInquiry, UUID> {

    Page<PublicScholarshipInquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
