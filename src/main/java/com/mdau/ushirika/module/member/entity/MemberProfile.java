package com.mdau.ushirika.module.member.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.auth.entity.User;
import com.mdau.ushirika.module.member.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "member_profiles",
    indexes = {
        // Common admin filters
        @Index(name = "idx_mp_county",       columnList = "county"),
        @Index(name = "idx_mp_gender",       columnList = "gender"),
        @Index(name = "idx_mp_member_since", columnList = "member_since")
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

    @Column(name = "id_number", unique = true, nullable = false, length = 20)
    private String idNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "county", nullable = false, length = 100)
    private String county;

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    @Column(name = "next_of_kin_name", nullable = false, length = 150)
    private String nextOfKinName;

    @Column(name = "next_of_kin_phone", nullable = false, length = 20)
    private String nextOfKinPhone;

    @Column(name = "next_of_kin_relationship", nullable = false, length = 50)
    private String nextOfKinRelationship;

    /** Assigned on membership approval — format: UW-YYYY-XXXX */
    @Column(name = "member_id", unique = true, length = 20)
    private String memberId;

    @Column(name = "member_since")
    private LocalDate memberSince;
}
