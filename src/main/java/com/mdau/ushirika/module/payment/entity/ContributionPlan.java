package com.mdau.ushirika.module.payment.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.payment.enums.ContributionFrequency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 15)
    private ContributionFrequency frequency;

    /** Ordered list of privilege/feature bullet-points shown on the membership page. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    /** Optional short badge shown on the plan card (e.g. "Most Common", "Best Value"). Null = no badge. */
    @Column(name = "badge", length = 50)
    private String badge;

    /** Controls the left-to-right display order on the membership page. Lower = first. */
    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
