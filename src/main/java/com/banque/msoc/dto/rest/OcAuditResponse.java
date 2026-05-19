package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.OcDecision;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OcAuditResponse {
    private OcDecision decision;
    private boolean finalizeFlag;
    private String userId;
    private String roleCode;
    private String orgNodeId;
    private String reason;
    private String commentText;
    private String correlationId;
    private LocalDateTime createdAt;
}
