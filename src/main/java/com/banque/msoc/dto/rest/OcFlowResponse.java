package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.domain.enums.SignatureStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OcFlowResponse {
    private String businessKey;
    private String flowReference;
    private String flowType;
    private String source;
    private SignatureStatus signatureStatus;
    private OcFlowStatus status;
    private String correlationId;
    private LocalDateTime receivedAt;
    private LocalDateTime finalizedAt;
    private OcFlowDetailResponse detail;  //détail complet
    private OcFlowDetailSummaryResponse summaryDetail; //résumé utile pour le tableau
    private LocalDateTime createdAt;
    private BigDecimal montantTotal;
}
