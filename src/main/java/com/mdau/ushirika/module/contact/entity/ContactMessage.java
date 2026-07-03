package com.mdau.ushirika.module.contact.entity;

import com.mdau.ushirika.common.entity.BaseEntity;
import com.mdau.ushirika.module.contact.enums.ContactMessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "contact_messages",
    indexes = {
        @Index(name = "idx_cm_status",      columnList = "status"),
        @Index(name = "idx_cm_email",       columnList = "email"),
        @Index(name = "idx_cm_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private ContactMessageStatus status = ContactMessageStatus.NEW;

    /** Timestamp when an admin first opened this message. */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /** Timestamp when an admin marked this as replied. */
    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    /** Admin username who last updated the status. */
    @Column(name = "handled_by", length = 150)
    private String handledBy;

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;
}
