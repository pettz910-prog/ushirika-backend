package com.mdau.ushirika.module.member.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.Gender;
import com.mdau.ushirika.module.member.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "member_profiles",
    indexes = {
        @Index(name = "idx_mp_county",       columnList = "county"),
        @Index(name = "idx_mp_gender",       columnList = "gender"),
        @Index(name = "idx_mp_member_since", columnList = "member_since"),
        @Index(name = "idx_mp_tier",         columnList = "membership_tier")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
                foreignKey = @ForeignKey(name = "fk_mp_user"))
    private User user;

    // ── Identity ──────────────────────────────────────────────────────────────

    @Column(name = "id_number", unique = true, nullable = false, length = 20)
    private String idNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    // ── Address ───────────────────────────────────────────────────────────────

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "county", nullable = false, length = 100)
    private String county;

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    // ── Family ────────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "spouse_name", length = 150)
    private String spouseName;

    /** JSON array: [{"name":"...","dateOfBirth":"YYYY-MM-DD"},...] */
    @Column(name = "children_json", columnDefinition = "TEXT")
    private String childrenJson;

    // ── Next of Kin ───────────────────────────────────────────────────────────

    @Column(name = "next_of_kin_name", nullable = false, length = 150)
    private String nextOfKinName;

    @Column(name = "next_of_kin_phone", nullable = false, length = 20)
    private String nextOfKinPhone;

    @Column(name = "next_of_kin_relationship", nullable = false, length = 50)
    private String nextOfKinRelationship;

    // ── Emergency Contact ─────────────────────────────────────────────────────

    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    // ── Occupation ────────────────────────────────────────────────────────────

    @Column(name = "occupation", length = 150)
    private String occupation;

    @Column(name = "employer", length = 200)
    private String employer;

    // ── Member References ─────────────────────────────────────────────────────

    @Column(name = "reference1_name", length = 150)
    private String reference1Name;

    @Column(name = "reference1_member_id", length = 20)
    private String reference1MemberId;

    @Column(name = "reference2_name", length = 150)
    private String reference2Name;

    @Column(name = "reference2_member_id", length = 20)
    private String reference2MemberId;

    // ── Discovery ─────────────────────────────────────────────────────────────

    @Column(name = "heard_about_us", length = 200)
    private String heardAboutUs;

    // ── Membership ────────────────────────────────────────────────────────────

    /** Assigned on membership approval — format: UW-YYYY-XXXX */
    @Column(name = "member_id", unique = true, length = 20)
    private String memberId;

    @Column(name = "member_since")
    private LocalDate memberSince;

    /**
     * Contribution plan tier name — matches ContributionPlan.name (Standard / Family / Patron).
     * Set to "Standard" on membership approval; updated by admin when member upgrades.
     */
    @Column(name = "membership_tier", length = 50)
    @Builder.Default
    private String membershipTier = "Standard";
}
