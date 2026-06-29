package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.dto.kafka.OcOutboundEventResponse;
import com.banque.msoc.dto.rest.*;
import com.banque.msoc.exception.FlowNotFoundException;
import com.banque.msoc.mapper.OcFlowMapper;
import com.banque.msoc.repository.OcDecisionAuditRepository;
import com.banque.msoc.repository.OcFlowPayloadRepository;
import com.banque.msoc.repository.OcFlowRepository;
import com.banque.msoc.repository.OcOutboundEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcFlowQueryService {
    private final OcFlowRepository flowRepository;
    private final OcDecisionAuditRepository auditRepository;
    private final OcFlowPayloadRepository payloadRepository;
    private final OcOutboundEventRepository outboundEventRepository;
    private final OcFlowMapper mapper;

    private Pageable applyDefaultCreatedAtDescSort(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    @Transactional(readOnly = true)
    public Page<OcFlowResponse> search(OcFlowSearchCriteria criteria, Pageable pageable) {
        Pageable sortedPageable = applyDefaultCreatedAtDescSort(pageable);

        return flowRepository.findAll(spec(criteria), sortedPageable)
                .map(mapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OcFlowResponse getByBusinessKey(String businessKey) {
        OcFlow flow = flowRepository.findWithDetailByBusinessKey(businessKey)
                .orElseThrow(() -> new FlowNotFoundException(businessKey));

        return mapper.toResponse(flow);
    }

    @Transactional(readOnly = true)
    public List<OcAuditResponse> getAudit(String businessKey) {
        return auditRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey).stream().map(mapper::toAuditResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<OcPayloadResponse> getPayloads(String businessKey) {
        return payloadRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey).stream().map(mapper::toPayloadResponse).toList();
    }

    private Specification<OcFlow> spec(OcFlowSearchCriteria c) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (c.getStatus() != null) predicate = cb.and(predicate, cb.equal(root.get("status"), c.getStatus()));
            if (c.getFlowReference() != null) predicate = cb.and(predicate, cb.like(cb.upper(root.get("flowReference")), "%" + c.getFlowReference().toUpperCase() + "%"));
            if (c.getFlowType() != null) predicate = cb.and(predicate, cb.equal(root.get("flowType"), c.getFlowType()));
            if (c.getDateFrom() != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("receivedAt"), c.getDateFrom().atStartOfDay()));
            if (c.getDateTo() != null) predicate = cb.and(predicate, cb.lessThan(root.get("receivedAt"), c.getDateTo().plusDays(1).atStartOfDay()));
            return predicate;
        };
    }

    @Transactional(readOnly = true)
    public List<OcOutboundEventResponse> getOutboundEvents(String businessKey) {
        return outboundEventRepository.findByFlowBusinessKeyOrderByCreatedAtAsc(businessKey)
                .stream()
                .map(mapper::toOutboundEventResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OcStatsResponse getStats() {
        long totalFlux = flowRepository.count();

        long fluxAcceptes = auditRepository.countLatestAccepted();

        long fluxRejetes = auditRepository.countLatestRejected();

        long fluxEnAttente = totalFlux - fluxAcceptes - fluxRejetes;

        long fluxErreurOutbound = outboundEventRepository.countByStatus(EventStatus.FAILED);

        return OcStatsResponse.builder()
                .totalFlux(totalFlux)
                .fluxEnAttente(fluxEnAttente)
                .fluxAcceptes(fluxAcceptes)
                .fluxRejetes(fluxRejetes)
                .fluxErreurOutbound(fluxErreurOutbound)
                .build();
    }

    @Transactional(readOnly = true)
    public List<OcFlowActivityResponse> getActivityLast7Days() {
        return flowRepository.countFlowsByCreatedAtLast7Days()
                .stream()
                .map(row -> OcFlowActivityResponse.builder()
                        .dateKey(row.getDateKey())
                        .jour(row.getJour())
                        .flux(row.getFlux() == null ? 0L : row.getFlux())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public OcOutboundDashboardResponse getOutboundDashboard() {

        long enAttenteEnvoi = outboundEventRepository.countByStatus(EventStatus.PENDING);

        long envoyes = outboundEventRepository.countByStatus(EventStatus.SENT);

        long enErreur = outboundEventRepository.countByStatus(EventStatus.FAILED);

        List<OcOutboundEventResponse> derniersFluxSortants =
                outboundEventRepository.findTop5ByOrderByCreatedAtDesc()
                        .stream()
                        .map(mapper::toOutboundEventResponse)
                        .toList();

        return OcOutboundDashboardResponse.builder()
                .enAttenteEnvoi(enAttenteEnvoi)
                .envoyes(envoyes)
                .enErreur(enErreur)
                .derniersFluxSortants(derniersFluxSortants)
                .build();
    }

    @Transactional(readOnly = true)
    public List<OcTimelineEventResponse> getRecentTimeline() {
        List<OcTimelineEventResponse> events = new java.util.ArrayList<>();

        flowRepository.findTop10ByOrderByCreatedAtDesc()
                .forEach(flow -> events.add(
                        OcTimelineEventResponse.builder()
                                .timestamp(flow.getCreatedAt())
                                .type("FLUX_RECU")
                                .description("Flux " + safe(flow.getFlowReference()) + " reçu")
                                .utilisateur(flow.getSource() != null ? flow.getSource() : "SYSTÈME")
                                .build()
                ));

        auditRepository.findTop10ByOrderByCreatedAtDesc()
                .forEach(audit -> {
                    String decision = audit.getDecision() != null
                            ? audit.getDecision().name()
                            : "";

                    String type;
                    String action;

                    if ("ACCEPT".equals(decision)) {
                        type = "ACCEPTATION";
                        action = "accepté";
                    } else if ("REJECT".equals(decision)) {
                        type = "REJET";
                        action = "rejeté";
                    } else {
                        type = "CONSULTATION";
                        action = "traité";
                    }

                    String reference = audit.getFlow() != null
                            ? audit.getFlow().getFlowReference()
                            : "-";

                    events.add(
                            OcTimelineEventResponse.builder()
                                    .timestamp(audit.getCreatedAt())
                                    .type(type)
                                    .description("Flux " + safe(reference) + " " + action)
                                    .utilisateur(
                                            audit.getUserId() != null
                                                    ? audit.getUserId()
                                                    : audit.getRoleCode()
                                    )
                                    .build()
                    );
                });

        outboundEventRepository.findTop10ByOrderByCreatedAtDesc()
                .forEach(event -> {
                    String reference = event.getFlow() != null
                            ? event.getFlow().getFlowReference()
                            : "flux sortant";

                    boolean failed = event.getStatus() == EventStatus.FAILED;

                    LocalDateTime eventDate = event.getSentAt() != null
                            ? event.getSentAt()
                            : event.getCreatedAt();

                    events.add(
                            OcTimelineEventResponse.builder()
                                    .timestamp(eventDate)
                                    .type(failed ? "ERREUR" : "FLUX_SORTANT")
                                    .description(
                                            failed
                                                    ? "Erreur flux sortant " + safe(reference)
                                                    : "Flux sortant " + safe(reference) + " envoyé"
                                    )
                                    .utilisateur("SYSTÈME")
                                    .build()
                    );
                });

        return events.stream()
                .filter(e -> e.getTimestamp() != null)
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(8)
                .toList();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
