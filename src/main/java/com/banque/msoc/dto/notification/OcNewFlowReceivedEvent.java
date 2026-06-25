package com.banque.msoc.dto.notification;

import java.time.LocalDateTime;

public record OcNewFlowReceivedEvent(    String businessKey,
                                         String numeroDossier,
                                         String numeroDemande,
                                         String statutMetier,
                                         String signatureStatus,
                                         LocalDateTime receivedAt,
                                         String sourceEventId,
                                         String correlationId) {
}
