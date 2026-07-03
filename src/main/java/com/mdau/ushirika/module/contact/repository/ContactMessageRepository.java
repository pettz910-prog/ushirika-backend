package com.mdau.ushirika.module.contact.repository;

import com.mdau.ushirika.module.contact.entity.ContactMessage;
import com.mdau.ushirika.module.contact.enums.ContactMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {

    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    List<ContactMessage> findAllByStatusOrderByCreatedAtDesc(ContactMessageStatus status);

    @Query("SELECT COUNT(m) FROM ContactMessage m WHERE m.status = 'NEW'")
    long countNew();
}
