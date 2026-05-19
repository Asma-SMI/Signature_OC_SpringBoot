package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.domain.enums.SignatureStatus;
import lombok.Builder;
import lombok.Data;

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
    private OcFlowDetailResponse detail;
}
