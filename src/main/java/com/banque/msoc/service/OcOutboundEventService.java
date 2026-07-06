package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.entity.OcOutboundEvent;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.PayloadType;
import com.banque.msoc.dto.kafka.OcInboundPayloadDto;
import com.banque.msoc.dto.kafka.OcOutboundKafkaMessage;
import com.banque.msoc.dto.rest.OcDecisionRequest;
import com.banque.msoc.repository.OcOutboundEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcOutboundEventService {

    private final OcOutboundEventRepository repository;
    private final OcPayloadService payloadService;
    private final ObjectMapper objectMapper;

    @Value("${msoc.kafka.outbound-topic}")
    private String outboundTopic;

    @Transactional
    public OcOutboundEvent createPendingOutboundEvent(
            OcFlow flow,
            OcDecisionRequest request,
            String decisionUser
    ) {
        OcInboundPayloadDto responsePayload = buildResponsePayload(flow, request);

        OcOutboundKafkaMessage message = OcOutboundKafkaMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .correlationId(flow.getCorrelationId())
                .businessKey(flow.getBusinessKey())
                .decision(request.getDecision())
                .dossierStatus(flow.getStatus())
                .requestedAction("GENERATE_AND_SIGN")
                .timestamp(LocalDateTime.now())
                .responsePayload(responsePayload)
                .build();

        try {
            String json = objectMapper.writeValueAsString(message);

            OcOutboundEvent event = repository.save(
                    OcOutboundEvent.builder()
                            .flow(flow)
                            .messageId(message.getMessageId())
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
            throw new IllegalStateException("Impossible de créer le message sortant", e);
        }
    }

    private OcInboundPayloadDto buildResponsePayload(
            OcFlow flow,
            OcDecisionRequest request
    ) {
        OcInboundPayloadDto payload = loadOriginalInboundPayloadDto(flow);

        boolean accepted = request.getDecision() == OcDecision.ACCEPT;

        String outboundTypeDocument = accepted ? "O04" : "O03";
        String decisionCode = accepted ? "ACCEPT" : "REJECT";
        String decisionLabel = accepted ? "Acceptation bancaire" : "Rejet bancaire";

        payload.setTypeDocument(outboundTypeDocument);

        payload.setCodeDecisionBanque(decisionCode);
        payload.setLibelleDecision(decisionLabel);
        payload.setMotifRejet(accepted ? null : request.getReason());

        return payload;
    }

    private OcInboundPayloadDto loadOriginalInboundPayloadDto(OcFlow flow) {
        Object inboundPayload = payloadService.getLatestPayload(
                flow,
                PayloadType.INBOUND
        );

        if (inboundPayload == null) {
            throw new IllegalStateException(
                    "Payload inbound introuvable pour le flux " + flow.getBusinessKey()
            );
        }

        Map<String, Object> root = objectMapper.convertValue(
                inboundPayload,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {}
        );

        Object innerPayload = root.get("payload");

        if (innerPayload != null) {
            return objectMapper.convertValue(innerPayload, OcInboundPayloadDto.class);
        }

        return objectMapper.convertValue(root, OcInboundPayloadDto.class);
    }
}