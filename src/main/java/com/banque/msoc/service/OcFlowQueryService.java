package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.dto.kafka.OcOutboundEventResponse;
import com.banque.msoc.dto.rest.*;
import com.banque.msoc.exception.FlowNotFoundException;
import com.banque.msoc.mapper.OcFlowMapper;
import com.banque.msoc.repository.OcDecisionAuditRepository;
import com.banque.msoc.repository.OcFlowPayloadRepository;
import com.banque.msoc.repository.OcFlowRepository;
import com.banque.msoc.repository.OcOutboundEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcFlowQueryService {
    private final OcFlowRepository flowRepository;
    private final OcDecisionAuditRepository auditRepository;
    private final OcFlowPayloadRepository payloadRepository;
    private final OcOutboundEventRepository outboundEventRepository;
    private final OcFlowMapper mapper;

    @Transactional(readOnly = true)
    public Page<OcFlowResponse> search(OcFlowSearchCriteria criteria, Pageable pageable) {
        return flowRepository.findAll(spec(criteria), pageable).map(mapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OcFlowResponse getByBusinessKey(String businessKey) {
        OcFlow flow = flowRepository.findWithDetailByBusinessKey(businessKey)
                .orElseThrow(() -> new FlowNotFoundException(businessKey));

        return mapper.toResponse(flow);
    }

    @Transactional(readOnly = true)
    public List<OcAuditResponse> getAudit(String businessKey) {
        return auditRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey).stream().map(mapper::toAuditResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OcPayloadResponse> getPayloads(String businessKey) {
        return payloadRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey).stream().map(mapper::toPayloadResponse).toList();
    }

    private Specification<OcFlow> spec(OcFlowSearchCriteria c) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (c.getStatus() != null) predicate = cb.and(predicate, cb.equal(root.get("status"), c.getStatus()));
            if (c.getFlowReference() != null) predicate = cb.and(predicate, cb.like(cb.upper(root.get("flowReference")), "%" + c.getFlowReference().toUpperCase() + "%"));
            if (c.getFlowType() != null) predicate = cb.and(predicate, cb.equal(root.get("flowType"), c.getFlowType()));
            if (c.getDateFrom() != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("receivedAt"), c.getDateFrom().atStartOfDay()));
            if (c.getDateTo() != null) predicate = cb.and(predicate, cb.lessThan(root.get("receivedAt"), c.getDateTo().plusDays(1).atStartOfDay()));
            return predicate;
        };
    }

    @Transactional(readOnly = true)
    public List<OcOutboundEventResponse> getOutboundEvents(String businessKey) {
        return outboundEventRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey)
                .stream()
                .map(mapper::toOutboundEventResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OcStatsResponse getStats() {
        return OcStatsResponse.builder()
                .totalFlux(flowRepository.count())
                .fluxErreurOutbound(outboundEventRepository.countByStatus(EventStatus.FAILED))
                .build();
    }
}
