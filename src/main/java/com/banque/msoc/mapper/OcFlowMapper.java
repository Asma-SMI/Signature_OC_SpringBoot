package com.banque.msoc.mapper;

import com.banque.msoc.domain.entity.OcDecisionAudit;
import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcFlowDetail;
import com.banque.msoc.domain.entity.OcFlowPayload;
import com.banque.msoc.dto.rest.*;
import org.springframework.stereotype.Component;

@Component
public class OcFlowMapper {

    public OcFlowDetailSummaryResponse toSummaryDetailResponse(OcFlowDetail d) {
       if (d == null) {
           return null;
       }

       return OcFlowDetailSummaryResponse.builder()
               .codTypMes(d.getCodTypMes())
               .codTypDoc(d.getCodTypDoc())
               .etat(d.getEtat())

               .numDossTtn(d.getNumDossTtn())
               .numDemTtn(d.getNumDemTtn())
               .numMessTtn(d.getNumMessTtn())


               .build();
   }
    public OcFlowResponse toSummaryResponse(OcFlow flow) {
        if (flow == null) {
            return null;
        }

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
                .summaryDetail(toSummaryDetailResponse(flow.getDetail()))
                .detail(null)
                .build();
    }

    public OcFlowResponse toResponse(OcFlow flow) {
        if (flow == null) {
            return null;
        }

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
                .summaryDetail(null)
                .build();
    }

    public OcFlowDetailResponse toDetailResponse(OcFlowDetail d) {
        if (d == null) {
            return null;
        }

        return OcFlowDetailResponse.builder()
                .codTypMes(d.getCodTypMes())
                .codTypDoc(d.getCodTypDoc())
                .etat(d.getEtat())

                .numMessTtn(d.getNumMessTtn())
                .numDossTtn(d.getNumDossTtn())
                .numDemTtn(d.getNumDemTtn())

                .emetteur(d.getEmetteur())
                .destinataire(d.getDestinataire())

                .codDouImp(d.getCodDouImp())
                .raiSocImp(d.getRaiSocImp())
                .adresseImp(d.getAdresseImp())

                .codTtnDec(d.getCodTtnDec())
                .nomSigDec(d.getNomSigDec())
                .datDec(d.getDatDec())

                .codBurDou(d.getCodBurDou())
                .libBurDou(d.getLibBurDou())
                .codCpt(d.getCodCpt())

                .numRepDdm(d.getNumRepDdm())

                .numDecDdm(d.getNumDecDdm())
                .datDecDdm(d.getDatDecDdm())

                .codBqImp(d.getCodBqImp())
                .libBqImp(d.getLibBqImp())
                .codOrgImp(d.getCodOrgImp())
                .libOrgImp(d.getLibOrgImp())
                .numRib(d.getNumRib())

                .numEnrOc(d.getNumEnrOc())
                .datEnrOc(d.getDatEnrOc())

                .montPrincipal(d.getMontPrincipal())
                .montInteret(d.getMontInteret())
                .montTot(d.getMontTot())
                .montLettre(d.getMontLettre())
                .montRemise(d.getMontRemise())
                .delaiPaie(d.getDelaiPaie())
                .datEch(d.getDatEch())

                .codDecBq(d.getCodDecBq())
                .libDec(d.getLibDec())
                .libCaution(d.getLibCaution())
                .nomOrgBq(d.getNomOrgBq())
                .nomSigBq(d.getNomSigBq())
                .datSigBq(d.getDatSigBq())
                .motifRejet(d.getMotifRejet())

                .numQuittance(d.getNumQuittance())
                .datQuittance(d.getDatQuittance())

                .nomSigRec(d.getNomSigRec())
                .datSigRec(d.getDatSigRec())

                .idSeq(d.getIdSeq())
                .indTransact(d.getIndTransact())
                .motifAnnul(d.getMotifAnnul())

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
