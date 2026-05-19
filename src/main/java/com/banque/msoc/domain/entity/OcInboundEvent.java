package com.banque.msoc.domain.entity;

import com.banque.msoc.domain.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_INBOUND_EVENT")
public class OcInboundEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_inbound_event_seq")
    @SequenceGenerator(name = "oc_inbound_event_seq", sequenceName = "SEQ_OC_INBOUND_EVENT", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MESSAGE_ID", unique = true, nullable = false, length = 100)
    private String messageId;
    @Column(name = "CORRELATION_ID", length = 100) private String correlationId;
    @Column(name = "TOPIC", length = 100) private String topic;
    @Column(name = "PARTITION_NO") private Integer partitionNo;
    @Column(name = "OFFSET_NO") private Long offsetNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private EventStatus status;

    @Column(name = "ERROR_MESSAGE", length = 1000) private String errorMessage;
    @Column(name = "RECEIVED_AT", nullable = false) private LocalDateTime receivedAt;
    @Column(name = "PROCESSED_AT") private LocalDateTime processedAt;

    @PrePersist
    void prePersist() { if (receivedAt == null) receivedAt = LocalDateTime.now(); }
}
