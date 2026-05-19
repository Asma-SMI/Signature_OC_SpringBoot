package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.enums.OcFlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OcFlowRepository extends JpaRepository<OcFlow, Long>, JpaSpecificationExecutor<OcFlow> {
    Optional<OcFlow> findByBusinessKey(String businessKey);
    boolean existsByBusinessKey(String businessKey);
    boolean existsByInboundMessageId(String inboundMessageId);
    long countByStatus(OcFlowStatus status);
}
