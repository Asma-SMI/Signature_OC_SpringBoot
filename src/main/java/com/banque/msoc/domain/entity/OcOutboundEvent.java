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
@Table(name = "OC_OUTBOUND_EVENT")
public class OcOutboundEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_outbound_event_seq")
    @SequenceGenerator(name = "oc_outbound_event_seq", sequenceName = "SEQ_OC_OUTBOUND_EVENT", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLOW_ID", nullable = false)
    private OcFlow flow;

    @Column(name = "MESSAGE_ID", unique = true, nullable = false, length = 100)
    private String messageId;
    @Column(name = "TOPIC", nullable = false, length = 100) private String topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private EventStatus status;

    @Lob
    @Column(name = "PAYLOAD_JSON", nullable = false)
    private String payloadJson;

    @Column(name = "ERROR_MESSAGE", length = 1000) private String errorMessage;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "SENT_AT") private LocalDateTime sentAt;
    @Column(name = "RETRY_COUNT") private Integer retryCount;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (retryCount == null) retryCount = 0;
    }
}
