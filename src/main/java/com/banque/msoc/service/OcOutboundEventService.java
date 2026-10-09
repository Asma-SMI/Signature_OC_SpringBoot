package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcOutboundEvent;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.PayloadType;
import com.banque.msoc.dto.kafka.OcInboundPayloadDto;
import com.banque.msoc.dto.kafka.station.StationOcOutboundDto;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.mapper.StationOcOutboundMapper;
import com.banque.msoc.repository.OcOutboundEventRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OcOutboundEventService {

    private final OcOutboundEventRepository repository;
    private final OcPayloadService payloadService;
    private final ObjectMapper objectMapper;
    private final StationOcOutboundMapper outboundMapper;

    @Value("${msoc.kafka.outbound-topic}")
    private String outboundTopic;

    @Transactional
    public OcOutboundEvent createPendingOutboundEvent(
            OcFlow flow,
            OcDecisionRequest request,
            String decisionUser
    ) {
        // 1. Recuperer le payload metier
        OcInboundPayloadDto responsePayload =
                buildResponsePayload(flow, request);

        // 2. Recuperer les metadonnees du flux entrant
        Map<String, Object> inboundRoot =
                loadInboundRoot(flow);

        String version = getInboundHeaderValue(
                inboundRoot, "version"
        );

        String partner = getInboundHeaderValue(
                inboundRoot, "partner"
        );

        // 3. Construire le nouveau contrat sortant
        StationOcOutboundDto message =
                outboundMapper.map(
                        responsePayload,
                        flow.getCorrelationId(),
                        version,
                        partner,
                        request.getDecision()
                );

        try {
            ObjectMapper outboundJsonMapper =
                    objectMapper.copy();

            outboundJsonMapper.setSerializationInclusion(
                    JsonInclude.Include.ALWAYS
            );

            String json =
                    outboundJsonMapper.writeValueAsString(message);

            OcOutboundEvent event = repository.save(
                    OcOutboundEvent.builder()
                            .flow(flow)
                            .messageId(
                                    message.getHeader().getRequestId()
                            )
                            .topic(outboundTopic)
                            .status(EventStatus.PENDING)
                            .payloadJson(json)
                            .build()
            );

            payloadService.savePayload(
                    flow,
                    PayloadType.OUTBOUND_REQUEST,
                    message,
                    decisionUser
            );

            return event;

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Impossible de creer le message sortant pour le dossier "
                            + flow.getBusinessKey(),
                    e
            );
        }
    }


    private OcInboundPayloadDto buildResponsePayload(
            OcFlow flow,
            OcDecisionRequest request
    ) {

        // Recuperer le payload du flux entrant
        OcInboundPayloadDto payload =
                loadOriginalInboundPayloadDto(flow);


        if (request.getDecision() == OcDecision.REJECT) {

            payload.setMotifRejet(
                    request.getReason()
            );

        } else {

            payload.setMotifRejet(null);

        }

        return payload;
    }

    private OcInboundPayloadDto loadOriginalInboundPayloadDto(
            OcFlow flow
    ) {

        Object inboundPayload =
                payloadService.getLatestPayload(
                        flow,
                        PayloadType.INBOUND
                );

        if (inboundPayload == null) {

            throw new IllegalStateException(
                    "Payload inbound introuvable pour le flux "
                            + flow.getBusinessKey()
            );
        }

        Map<String, Object> root =
                objectMapper.convertValue(
                        inboundPayload,
                        new TypeReference<Map<String, Object>>() {}
                );

        Object innerPayload = root.get("payload");

        if (innerPayload != null) {

            return objectMapper.convertValue(
                    innerPayload,
                    OcInboundPayloadDto.class
            );
        }

        return objectMapper.convertValue(
                root,
                OcInboundPayloadDto.class
        );
    }

    private Map<String, Object> loadInboundRoot(OcFlow flow) {

        Object inboundPayload =
                payloadService.getLatestPayload(
                        flow,
                        PayloadType.INBOUND
                );

        if (inboundPayload == null) {
            throw new IllegalStateException(
                    "Payload inbound introuvable pour le flux "
                            + flow.getBusinessKey()
            );
        }

        return objectMapper.convertValue(
                inboundPayload,
                new TypeReference<Map<String, Object>>() {}
        );
    }

    private String getInboundHeaderValue(
            Map<String, Object> root,
            String field
    ) {
        // Nouveau format station archive directement
        Object header = root.get("header");

        if (header instanceof Map<?, ?> headerMap) {
            Object value = headerMap.get(field);
            if (value != null) {
                return value.toString();
            }
        }

        Object value = root.get(field);

        if (value != null) {
            return value.toString();
        }

        throw new IllegalStateException(
                "Champ obligatoire du header entrant introuvable : "
                        + field
        );
    }



}