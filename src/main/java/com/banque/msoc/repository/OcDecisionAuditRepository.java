package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcDecisionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcDecisionAuditRepository extends JpaRepository<OcDecisionAudit, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    List<OcDecisionAudit> findByFlowBusinessKeyOrderByCreatedAtAsc(String businessKey);
}
