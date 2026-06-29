package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcDecisionAudit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OcDecisionAuditRepository extends JpaRepository<OcDecisionAudit, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    List<OcDecisionAudit> findByFlowBusinessKeyOrderByCreatedAtAsc(String businessKey);

    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT x.FLOW_ID, x.DECISION
            FROM (
                SELECT 
                    a.FLOW_ID,
                    a.DECISION,
                    ROW_NUMBER() OVER (
                        PARTITION BY a.FLOW_ID
                        ORDER BY a.CREATED_AT DESC, a.ID DESC
                    ) AS rn
                FROM OC_DECISION_AUDIT a
            ) x
            WHERE x.rn = 1
              AND UPPER(x.DECISION) = 'ACCEPT'
        )
        """, nativeQuery = true)
    long countLatestAccepted();

    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT x.FLOW_ID, x.DECISION
            FROM (
                SELECT 
                    a.FLOW_ID,
                    a.DECISION,
                    ROW_NUMBER() OVER (
                        PARTITION BY a.FLOW_ID
                        ORDER BY a.CREATED_AT DESC, a.ID DESC
                    ) AS rn
                FROM OC_DECISION_AUDIT a
            ) x
            WHERE x.rn = 1
              AND UPPER(x.DECISION) = 'REJECT'
        )
        """, nativeQuery = true)
    long countLatestRejected();

    @EntityGraph(attributePaths = {"flow"})
    List<OcDecisionAudit> findTop10ByOrderByCreatedAtDesc();
}
