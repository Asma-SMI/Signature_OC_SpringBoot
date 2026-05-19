package com.banque.msoc.dto.kafka;

import com.banque.msoc.domain.enums.SignatureStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class OcInboundKafkaMessage {
    private String messageId;
    private String correlationId;
    private String source;
    private String flowType;
    private SignatureStatus signatureStatus;
    private LocalDateTime receivedAt;
    private OcInboundPayloadDto payload;
    private Map<String, Object> technicalMetadata;
}
