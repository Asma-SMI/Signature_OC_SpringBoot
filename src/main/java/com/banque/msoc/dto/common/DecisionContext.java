package com.banque.msoc.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DecisionContext {
    private String userId;
    private String roleCode;
    private String orgNodeId;
    private String correlationId;
    private String idempotencyKey;
}
