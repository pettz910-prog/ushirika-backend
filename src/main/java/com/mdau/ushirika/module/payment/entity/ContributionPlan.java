package com.mdau.ushirika.module.payment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.payment.enums.ContributionFrequency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "contribution_plans",
    indexes = {
        // Public plan listing only returns active plans
        @Index(name = "idx_cp_active",     columnList = "active"),
        @Index(name = "idx_cp_frequency",  columnList = "frequency")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionPlan extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "KES";

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 15)
    private ContributionFrequency frequency;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
