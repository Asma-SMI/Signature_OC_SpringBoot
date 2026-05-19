package com.banque.msoc.mapper;

import com.banque.msoc.domain.entity.OcDecisionAudit;
import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcFlowDetail;
import com.banque.msoc.domain.entity.OcFlowPayload;
import com.banque.msoc.dto.rest.*;
import org.springframework.stereotype.Component;

@Component
public class OcFlowMapper {
    public OcFlowResponse toResponse(OcFlow flow) {
        return OcFlowResponse.builder()
                .businessKey(flow.getBusinessKey())
                .flowReference(flow.getFlowReference())
                .flowType(flow.getFlowType())
                .source(flow.getSource())
                .signatureStatus(flow.getSignatureStatus())
                .status(flow.getStatus())
                .correlationId(flow.getCorrelationId())
                .receivedAt(flow.getReceivedAt())
                .finalizedAt(flow.getFinalizedAt())
                .detail(flow.getDetail() == null ? null : toDetailResponse(flow.getDetail()))
                .build();
    }

    public OcFlowDetailResponse toDetailResponse(OcFlowDetail detail) {
        return OcFlowDetailResponse.builder()
                .codTypMes(detail.getCodTypMes())
                .codTypDoc(detail.getCodTypDoc())
                .numMessTtn(detail.getNumMessTtn())
                .numDossTtn(detail.getNumDossTtn())
                .numDemTtn(detail.getNumDemTtn())
                .emetteur(detail.getEmetteur())
                .destinataire(detail.getDestinataire())
                .numRib(detail.getNumRib())
                .montPrincipal(detail.getMontPrincipal())
                .montInteret(detail.getMontInteret())
                .montTot(detail.getMontTot())
                .datEch(detail.getDatEch())
                .motifRejet(detail.getMotifRejet())
                .build();
    }

    public OcAuditResponse toAuditResponse(OcDecisionAudit audit) {
        return OcAuditResponse.builder()
                .decision(audit.getDecision())
                .finalizeFlag(Boolean.TRUE.equals(audit.getFinalizeFlag()))
                .userId(audit.getUserId())
                .roleCode(audit.getRoleCode())
                .orgNodeId(audit.getOrgNodeId())
                .reason(audit.getReason())
                .commentText(audit.getCommentText())
                .correlationId(audit.getCorrelationId())
                .createdAt(audit.getCreatedAt())
                .build();
    }

    public OcPayloadResponse toPayloadResponse(OcFlowPayload payload) {
        return OcPayloadResponse.builder()
                .payloadType(payload.getPayloadType())
                .payloadJson(payload.getPayloadJson())
                .createdBy(payload.getCreatedBy())
                .createdAt(payload.getCreatedAt())
                .build();
    }
}
