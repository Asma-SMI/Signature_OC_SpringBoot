package com.banque.msoc.service;

import com.banque.msoc.audit.OcAuditService;
import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcOperation;
import com.banque.msoc.domain.enums.PayloadType;
import com.banque.msoc.dto.common.DecisionContext;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.dto.rest.OcDecisionResponse;
import com.banque.msoc.exception.DuplicateMessageException;
import com.banque.msoc.exception.FlowNotFoundException;
import com.banque.msoc.repository.OcDecisionAuditRepository;
import com.banque.msoc.repository.OcFlowRepository;
import com.banque.msoc.repository.OcOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OcFlowDecisionService {
    private final OcFlowRepository flowRepository;
    private final OcOperationRepository operationRepository;
    private final OcDecisionAuditRepository auditRepository;
    private final OcBusinessRulesService rulesService;
    private final OcPayloadService payloadService;
    private final OcAuditService auditService;
    private final OcOutboundEventService outboundEventService;

    @Transactional
    public OcDecisionResponse decide(String businessKey, boolean finalize, OcDecisionRequest request, DecisionContext context) {
        rulesService.validateDecisionInput(request.getDecision(), finalize, request.getReason());
        if (StringUtils.hasText(context.getIdempotencyKey()) && auditRepository.existsByIdempotencyKey(context.getIdempotencyKey())) {
            throw new DuplicateMessageException(context.getIdempotencyKey());
        }

        OcFlow flow = flowRepository.findByBusinessKey(businessKey).orElseThrow(() -> new FlowNotFoundException(businessKey));
        OcOperation operation = operationRepository.findFirstByBusinessKeyOrderByCreatedAtDesc(businessKey)
                .orElseGet(() -> operationRepository.save(OcOperation.builder()
                        .flow(flow)
                        .businessKey(flow.getBusinessKey())
                        .flowReference(flow.getFlowReference())
                        .flowType(flow.getFlowType())
                        .status(com.banque.msoc.domain.enums.OcOperationStatus.PENDING_REVIEW)
                        .finalized(false)
                        .build()));

        rulesService.assertFlowModifiable(flow);
        rulesService.assertOperationModifiable(operation);

        if (finalize) {
            rulesService.applyFinalDecision(flow, operation, request.getDecision(), context.getUserId());
            payloadService.savePayload(flow, PayloadType.FINAL_DECISION, request, context.getUserId());
            auditService.recordDecision(flow, operation, true, request, context);
            outboundEventService.createPendingOutboundEvent(flow, request, context.getUserId());
            return OcDecisionResponse.builder()
                    .businessKey(flow.getBusinessKey())
                    .operationStatus(operation.getStatus())
                    .dossierStatus(flow.getStatus())
                    .decision(request.getDecision())
                    .finalized(true)
                    .dossierImpacted(true)
                    .outboundPublished(true)
                    .message("Décision finale enregistrée, dossier impacté et événement Outbox créé.")
                    .build();
        }

        rulesService.applyIntermediateDecision(operation, request.getDecision());
        payloadService.savePayload(flow, PayloadType.DECISION_DRAFT, request, context.getUserId());
        auditService.recordDecision(flow, operation, false, request, context);
        return OcDecisionResponse.builder()
                .businessKey(flow.getBusinessKey())
                .operationStatus(operation.getStatus())
                .dossierStatus(flow.getStatus())
                .decision(request.getDecision())
                .finalized(false)
                .dossierImpacted(false)
                .outboundPublished(false)
                .message("Décision intermédiaire enregistrée sur l'opération. Le dossier n'a pas été impacté.")
                .build();
    }
}
