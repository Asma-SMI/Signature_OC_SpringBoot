package com.banque.msoc.domain.entity;

import com.banque.msoc.domain.enums.OcDecision;
import com.banque.msoc.domain.enums.OcOperationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OC_OPERATION", indexes = @Index(name = "IDX_OC_OPERATION_BK", columnList = "BUSINESS_KEY"))
public class OcOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_operation_seq")
    @SequenceGenerator(name = "oc_operation_seq", sequenceName = "SEQ_OC_OPERATION", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FLOW_ID", nullable = false)
    private OcFlow flow;

    @Column(name = "BUSINESS_KEY", length = 50) private String businessKey;
    @Column(name = "FLOW_REFERENCE", length = 50) private String flowReference;
    @Column(name = "FLOW_TYPE", length = 30) private String flowType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private OcOperationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "LAST_DECISION", length = 20)
    private OcDecision lastDecision;

    @Column(name = "FINALIZED", nullable = false)
    private Boolean finalized;
    @Column(name = "FINALIZED_AT") private LocalDateTime finalizedAt;
    @Column(name = "FINALIZED_BY", length = 50) private String finalizedBy;
    @Column(name = "CREATED_AT", nullable = false) private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT") private LocalDateTime updatedAt;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (finalized == null) finalized = false;
        if (version == null) version = 0L;
    }

    @PreUpdate
    void preUpdate() { updatedAt = LocalDateTime.now(); }
}
