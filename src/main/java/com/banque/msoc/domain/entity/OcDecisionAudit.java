package com.banque.msoc.domain.entity;

import com.banque.msoc.domain.enums.OcDecision;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_DECISION_AUDIT", indexes = @Index(name = "IDX_OC_DEC_AUDIT_CORR", columnList = "CORRELATION_ID"))
public class OcDecisionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_decision_audit_seq")
    @SequenceGenerator(name = "oc_decision_audit_seq", sequenceName = "SEQ_OC_DECISION_AUDIT", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLOW_ID", nullable = false)
    private OcFlow flow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATION_ID", nullable = false)
    private OcOperation operation;

    @Column(name = "IDEMPOTENCY_KEY", unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "DECISION", nullable = false, length = 20)
    private OcDecision decision;

    @Column(name = "FINALIZE_FLAG", nullable = false)
    private Boolean finalizeFlag;

    @Column(name = "USER_ID", length = 50) private String userId;
    @Column(name = "ROLE_CODE", length = 50) private String roleCode;
    @Column(name = "ORG_NODE_ID", length = 50) private String orgNodeId;
    @Column(name = "REASON", length = 500) private String reason;
    @Column(name = "COMMENT_TEXT", length = 1000) private String commentText;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "CORRELATION_ID", length = 100) private String correlationId;

    @PrePersist
    void prePersist() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
