package com.banque.msoc.domain.entity;

import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.domain.enums.SignatureStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_FLOW", indexes = {
        @Index(name = "IDX_OC_FLOW_BK", columnList = "BUSINESS_KEY"),
        @Index(name = "IDX_OC_FLOW_STATUS", columnList = "STATUS"),
        @Index(name = "IDX_OC_FLOW_CORR", columnList = "CORRELATION_ID"),
        @Index(name = "IDX_OC_FLOW_REF", columnList = "FLOW_REFERENCE")
})
public class OcFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_flow_seq")
    @SequenceGenerator(name = "oc_flow_seq", sequenceName = "SEQ_OC_FLOW", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BUSINESS_KEY", unique = true, nullable = false, length = 50)
    private String businessKey;

    @Column(name = "FLOW_REFERENCE", length = 50)
    private String flowReference;

    @Column(name = "FLOW_TYPE", nullable = false, length = 30)
    private String flowType;

    @Column(name = "SOURCE", length = 50)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "SIGNATURE_STATUS", nullable = false, length = 20)
    private SignatureStatus signatureStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private OcFlowStatus status;

    @Column(name = "CORRELATION_ID", length = 100)
    private String correlationId;

    @Column(name = "INBOUND_MESSAGE_ID", unique = true, length = 100)
    private String inboundMessageId;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "RECEIVED_AT")
    private LocalDateTime receivedAt;

    @Column(name = "FINALIZED_AT")
    private LocalDateTime finalizedAt;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @OneToOne(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OcFlowDetail detail;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OcOperation> operations = new ArrayList<>();

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OcFlowPayload> payloads = new ArrayList<>();

    public void attachDetail(OcFlowDetail detail) {
        this.detail = detail;
        detail.setFlow(this);
    }
}
