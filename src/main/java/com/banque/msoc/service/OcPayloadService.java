package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcFlowPayload;
import com.banque.msoc.domain.enums.PayloadType;
import com.banque.msoc.repository.OcFlowPayloadRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OcPayloadService {
    private final OcFlowPayloadRepository repository;
    private final ObjectMapper objectMapper;

    public OcFlowPayload savePayload(OcFlow flow, PayloadType type, Object payload, String createdBy) {
        try {
            String json = payload instanceof String s ? s : objectMapper.writeValueAsString(payload);
            return repository.save(OcFlowPayload.builder()
                    .flow(flow)
                    .payloadType(type)
                    .payloadJson(json)
                    .createdBy(createdBy)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de sérialiser le snapshot JSON", e);
        }
    }
}
