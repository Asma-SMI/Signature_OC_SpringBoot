package com.banque.msoc.dto.kafka;

import com.banque.msoc.domain.enums.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OcOutboundEventResponse {
    private String messageId;
    private String topic;
    private EventStatus status;
    private Integer retryCount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}