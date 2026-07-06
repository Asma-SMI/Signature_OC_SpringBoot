package com.banque.msoc.dto.kafka;

import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.OcFlowStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class OcOutboundKafkaMessage {
    private String messageId;
    private String correlationId;
    private String businessKey;
   // private String flowType;
    private OcDecision decision;
    private OcFlowStatus dossierStatus;
    private OcInboundPayloadDto responsePayload;
    private String requestedAction;
    private LocalDateTime timestamp;
}
