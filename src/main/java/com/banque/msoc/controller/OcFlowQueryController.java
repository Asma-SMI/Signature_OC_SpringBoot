package com.banque.msoc.controller;

import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.dto.rest.*;
import com.banque.msoc.service.OcFlowQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/oc/flows")
@RequiredArgsConstructor
public class OcFlowQueryController {
    private final OcFlowQueryService queryService;

    @GetMapping
    public Page<OcFlowResponse> search(
            @RequestParam(required = false) OcFlowStatus status,
            @RequestParam(required = false) String flowReference,
            @RequestParam(required = false) String flowType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Pageable pageable) {
        OcFlowSearchCriteria c = new OcFlowSearchCriteria();
        c.setStatus(status);
        c.setFlowReference(flowReference);
        c.setFlowType(flowType);
        c.setDateFrom(dateFrom);
        c.setDateTo(dateTo);
        return queryService.search(c, pageable);
    }

    @GetMapping("/{businessKey}")
    public OcFlowResponse get(@PathVariable String businessKey) {
        return queryService.getByBusinessKey(businessKey);
    }

    @GetMapping("/{businessKey}/audit")
    public List<OcAuditResponse> audit(@PathVariable String businessKey) {
        return queryService.getAudit(businessKey);
    }

    @GetMapping("/{businessKey}/payloads")
    public List<OcPayloadResponse> payloads(@PathVariable String businessKey) {
        return queryService.getPayloads(businessKey);
    }
}
