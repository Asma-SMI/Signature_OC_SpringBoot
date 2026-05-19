package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcFlowPayload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcFlowPayloadRepository extends JpaRepository<OcFlowPayload, Long> {
    List<OcFlowPayload> findByFlowBusinessKeyOrderByCreatedAtAsc(String businessKey);
}
