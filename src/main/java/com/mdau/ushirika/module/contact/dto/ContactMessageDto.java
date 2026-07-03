package com.mdau.ushirika.module.contact.dto;

import com.mdau.ushirika.module.contact.entity.ContactMessage;
import com.mdau.ushirika.module.contact.enums.ContactMessageStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContactMessageDto(
        UUID                  id,
        String                name,
        String                email,
        String                phone,
        String                subject,
        String                body,
        ContactMessageStatus  status,
        LocalDateTime         readAt,
        LocalDateTime         repliedAt,
        String                handledBy,
        String                adminNotes,
        LocalDateTime         createdAt
) {
    public static ContactMessageDto from(ContactMessage m) {
        return new ContactMessageDto(
                m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                m.getSubject(), m.getBody(), m.getStatus(),
                m.getReadAt(), m.getRepliedAt(), m.getHandledBy(),
                m.getAdminNotes(), m.getCreatedAt()
        );
    }
}
