package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcInboundEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OcInboundEventRepository extends JpaRepository<OcInboundEvent, Long> {
    boolean existsByMessageId(String messageId);
    Optional<OcInboundEvent> findByMessageId(String messageId);
}
