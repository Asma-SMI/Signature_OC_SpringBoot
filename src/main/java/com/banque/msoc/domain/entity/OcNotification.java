package com.banque.msoc.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "OC_NOTIFICATION")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICATION")
    private Long id;

    @Column(name = "EVENT_TYPE", nullable = false, length = 50)
    private String eventType;

    @Column(name = "TYPE_NOTIFICATION", nullable = false, length = 20)
    private String type;

    @Column(name = "CATEGORY", nullable = false, length = 30)
    private String category;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "MESSAGE", nullable = false, length = 1000)
    private String message;

    @Column(name = "BUSINESS_KEY", length = 100)
    private String businessKey;

    @Column(name = "NUMERO_DOSSIER", length = 100)
    private String numeroDossier;

    @Column(name = "NUMERO_DEMANDE", length = 100)
    private String numeroDemande;

    @Column(name = "STATUT_METIER", length = 50)
    private String statutMetier;

    @Column(name = "SIGNATURE_STATUS", length = 50)
    private String signatureStatus;

    @Column(name = "READ_FLAG", nullable = false, length = 1)
    private String readFlag;

    @Column(name = "READ_AT")
    private LocalDateTime readAt;

    @Column(name = "SOURCE_EVENT_ID", length = 100)
    private String sourceEventId;

    @Column(name = "CORRELATION_ID", length = 100)
    private String correlationId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
}
