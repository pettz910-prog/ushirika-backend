package com.mdau.ushirika.module.benevolence.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "benevolence_claim_categories",
    indexes = {
        @Index(name = "idx_bcc_active",      columnList = "active"),
        @Index(name = "idx_bcc_sort_order",  columnList = "sort_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenevolenceClaimCategory extends BaseEntity {

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    /** Label shown to the member for the "event date" field (e.g. "Date of Death", "Graduation Date"). */
    @Column(name = "event_date_label", length = 100)
    @Builder.Default
    private String eventDateLabel = "Event Date";

    /** Label shown to the member for the "person name" field (e.g. "Deceased's Name", "Graduate's Name"). */
    @Column(name = "event_person_label", length = 100)
    @Builder.Default
    private String eventPersonLabel = "Person Name";

    /** Whether document uploads are required for this category. */
    @Column(name = "requires_documents", nullable = false)
    @Builder.Default
    private boolean requiresDocuments = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
