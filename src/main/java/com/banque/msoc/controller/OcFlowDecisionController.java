package com.banque.msoc.controller;

import com.banque.msoc.dto.common.DecisionContext;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.dto.rest.OcDecisionResponse;
import com.banque.msoc.service.OcFlowDecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oc/flows")
@RequiredArgsConstructor
public class OcFlowDecisionController {
    private final OcFlowDecisionService decisionService;

    @PostMapping("/{businessKey}/decision")
    public ResponseEntity<OcDecisionResponse> decide(
            @PathVariable String businessKey,
            @RequestParam boolean finalize,
            @Valid @RequestBody OcDecisionRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Role-Code", required = false) String roleCode,
            @RequestHeader(value = "X-Org-Node-Id", required = false) String orgNodeId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        DecisionContext context = DecisionContext.builder()
                .userId(userId)
                .roleCode(roleCode)
                .orgNodeId(orgNodeId)
                .correlationId(correlationId)
                .idempotencyKey(idempotencyKey)
                .build();
        return ResponseEntity.ok(decisionService.decide(businessKey, finalize, request, context));
    }
}
