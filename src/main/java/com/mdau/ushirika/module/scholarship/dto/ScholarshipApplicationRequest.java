package com.mdau.ushirika.module.scholarship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ScholarshipApplicationRequest(

        @NotNull(message = "Program ID is required")
        UUID programId,

        /** Who will receive the scholarship — may be the member or their dependent. */
        @NotBlank(message = "Beneficiary name is required")
        String beneficiaryName,

        @NotBlank(message = "Institution name is required")
        String institutionName,

        @NotBlank(message = "Course of study is required")
        String courseOfStudy,

        @NotBlank(message = "Academic year is required")
        String academicYear,

        @NotBlank(message = "Personal statement is required")
        String personalStatement,

        /** Cloudinary URLs — admission letter, fee structure, academic transcripts. */
        List<String> documentUrls
) {}
