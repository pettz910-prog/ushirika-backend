package com.mdau.ushirika.module.contact.service;

import com.mdau.ushirika.module.contact.dto.ContactMessageDto;
import com.mdau.ushirika.module.contact.dto.ContactMessageRequest;
import com.mdau.ushirika.module.contact.dto.ContactMessageSubmittedDto;
import com.mdau.ushirika.module.contact.entity.ContactMessage;
import com.mdau.ushirika.module.contact.enums.ContactMessageStatus;
import com.mdau.ushirika.module.contact.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactMessageService {

    private final ContactMessageRepository repo;

    @Transactional
    public ContactMessageSubmittedDto submit(ContactMessageRequest req) {
        ContactMessage msg = ContactMessage.builder()
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .subject(req.subject())
                .body(req.body())
                .build();
        msg = repo.save(msg);
        // Reference code shown to the submitter — short and readable
        String ref = "MSG-" + msg.getId().toString().substring(0, 8).toUpperCase();
        return new ContactMessageSubmittedDto(msg.getId(), ref);
    }

    public List<ContactMessageDto> listAll() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(ContactMessageDto::from).toList();
    }

    public List<ContactMessageDto> listByStatus(ContactMessageStatus status) {
        return repo.findAllByStatusOrderByCreatedAtDesc(status).stream().map(ContactMessageDto::from).toList();
    }

    public long countNew() {
        return repo.countNew();
    }

    @Transactional
    public ContactMessageDto markRead(UUID id) {
        ContactMessage msg = findOrThrow(id);
        if (msg.getStatus() == ContactMessageStatus.NEW) {
            msg.setStatus(ContactMessageStatus.READ);
            msg.setReadAt(LocalDateTime.now());
            msg.setHandledBy(currentUser());
            msg = repo.save(msg);
        }
        return ContactMessageDto.from(msg);
    }

    @Transactional
    public ContactMessageDto markReplied(UUID id) {
        ContactMessage msg = findOrThrow(id);
        msg.setStatus(ContactMessageStatus.REPLIED);
        msg.setRepliedAt(LocalDateTime.now());
        msg.setHandledBy(currentUser());
        return ContactMessageDto.from(repo.save(msg));
    }

    @Transactional
    public ContactMessageDto archive(UUID id) {
        ContactMessage msg = findOrThrow(id);
        msg.setStatus(ContactMessageStatus.ARCHIVED);
        msg.setHandledBy(currentUser());
        return ContactMessageDto.from(repo.save(msg));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(findOrThrow(id));
    }

    private ContactMessage findOrThrow(UUID id) {
        return repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));
    }

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "admin";
    }
}
