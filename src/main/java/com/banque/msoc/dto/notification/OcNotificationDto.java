package com.banque.msoc.dto.notification;

import java.time.LocalDateTime;

public record OcNotificationDto( Long id,
                                 String type,
                                 String category,
                                 String eventType,
                                 String businessKey,
                                 String numeroDossier,
                                 String numeroDemande,
                                 String statutMetier,
                                 String signatureStatus,
                                 LocalDateTime receivedAt,
                                 String title,
                                 String message,
                                 boolean read) {
}
