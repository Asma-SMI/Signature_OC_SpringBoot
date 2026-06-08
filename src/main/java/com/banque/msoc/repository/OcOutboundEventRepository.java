package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcOutboundEvent;
import com.banque.msoc.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcOutboundEventRepository extends JpaRepository<OcOutboundEvent, Long> {
    List<OcOutboundEvent> findTop50ByStatusOrderByCreatedAtAsc(EventStatus status);
    List<OcOutboundEvent> findByFlowBusinessKeyOrderByCreatedAtAsc(String businessKey);
    long countByStatus(EventStatus status);

}
