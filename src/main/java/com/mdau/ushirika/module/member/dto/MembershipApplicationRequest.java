package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.member.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

public record MembershipApplicationRequest(

        @NotBlank(message = "National ID number is required")
        String idNumber,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotBlank(message = "Residential address is required")
        String address,

        @NotBlank(message = "County is required")
        String county,

        @NotBlank(message = "Next of kin name is required")
        String nextOfKinName,

        @NotBlank(message = "Next of kin phone is required")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid next of kin phone number")
        String nextOfKinPhone,

        @NotBlank(message = "Next of kin relationship is required")
        String nextOfKinRelationship,

        /** Cloudinary URLs for supporting documents uploaded before submission. */
        List<String> documentUrls
) {}
