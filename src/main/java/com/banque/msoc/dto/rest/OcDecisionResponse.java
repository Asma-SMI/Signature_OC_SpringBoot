package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.domain.enums.OcOperationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OcDecisionResponse {
    private String businessKey;
    private OcOperationStatus operationStatus;
    private OcFlowStatus dossierStatus;
    private OcDecision decision;
    private boolean finalized;
    private boolean dossierImpacted;
    private boolean outboundPublished;
    private String message;
}
