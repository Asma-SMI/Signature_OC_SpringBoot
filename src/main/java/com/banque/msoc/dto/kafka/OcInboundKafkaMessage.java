package com.banque.msoc.dto.kafka;

import com.banque.msoc.domain.enums.SignatureStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcInboundKafkaMessage {
    private String messageId;
    private String correlationId;
    private String source;
    private SignatureStatus signatureStatus;
    private LocalDateTime receivedAt;
    private OcInboundPayloadDto payload;
}
