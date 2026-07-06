package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcFlowPayload;
import com.banque.msoc.domain.enums.PayloadType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OcFlowPayloadRepository extends JpaRepository<OcFlowPayload, Long> {
    List<OcFlowPayload> findByFlowBusinessKeyOrderByCreatedAtAsc(String businessKey);
    Optional<OcFlowPayload> findTopByFlowAndPayloadTypeOrderByCreatedAtDesc(
            OcFlow flow,
            PayloadType payloadType
    );
}
