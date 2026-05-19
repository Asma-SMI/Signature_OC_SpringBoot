package com.banque.msoc.audit;

import com.banque.msoc.domain.entity.OcDecisionAudit;
import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcOperation;
import com.banque.msoc.dto.common.DecisionContext;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.repository.OcDecisionAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OcAuditService {
    private final OcDecisionAuditRepository repository;

    public OcDecisionAudit recordDecision(OcFlow flow, OcOperation operation, boolean finalize, OcDecisionRequest request, DecisionContext context) {
        return repository.save(OcDecisionAudit.builder()
                .flow(flow)
                .operation(operation)
                .decision(request.getDecision())
                .finalizeFlag(finalize)
                .idempotencyKey(context.getIdempotencyKey())
                .userId(context.getUserId())
                .roleCode(context.getRoleCode())
                .orgNodeId(context.getOrgNodeId())
                .reason(request.getReason())
                .commentText(request.getComment())
                .correlationId(context.getCorrelationId())
                .build());
    }
}
