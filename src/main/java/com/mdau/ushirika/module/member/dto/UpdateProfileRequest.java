package com.mdau.ushirika.module.member.dto;

import com.mdau.ushirika.module.member.enums.Gender;
import com.mdau.ushirika.module.member.enums.MaritalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        // ── Auth user fields ──────────────────────────────────────────────────
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9\\s\\-().]{7,20}$", message = "Invalid phone number format")
        String phone,

        // ── Identity ──────────────────────────────────────────────────────────
        @Size(max = 30) String idNumber,
        @NotNull Gender gender,
        @NotNull LocalDate dateOfBirth,

        // ── Address ───────────────────────────────────────────────────────────
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 100) String county,

        // ── Family ────────────────────────────────────────────────────────────
        MaritalStatus maritalStatus,
        @Size(max = 150) String spouseName,

        // ── Next of Kin ───────────────────────────────────────────────────────
        @NotBlank @Size(max = 150) String nextOfKinName,
        @NotBlank @Size(max = 20)  String nextOfKinPhone,
        @NotBlank @Size(max = 50)  String nextOfKinRelationship,

        // ── Emergency Contact ─────────────────────────────────────────────────
        @Size(max = 150) String emergencyContactName,
        @Size(max = 20)  String emergencyContactPhone,

        // ── Occupation ────────────────────────────────────────────────────────
        @Size(max = 150) String occupation,
        @Size(max = 200) String employer
) {}
