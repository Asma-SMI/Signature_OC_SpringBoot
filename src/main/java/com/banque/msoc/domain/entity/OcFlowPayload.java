package com.banque.msoc.domain.entity;

import com.banque.msoc.domain.enums.PayloadType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_FLOW_PAYLOAD")
public class OcFlowPayload {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_flow_payload_seq")
    @SequenceGenerator(name = "oc_flow_payload_seq", sequenceName = "SEQ_OC_FLOW_PAYLOAD", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLOW_ID", nullable = false)
    private OcFlow flow;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYLOAD_TYPE", nullable = false, length = 30)
    private PayloadType payloadType;

    @Lob
    @Column(name = "PAYLOAD_JSON", nullable = false)
    private String payloadJson;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @PrePersist
    void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
