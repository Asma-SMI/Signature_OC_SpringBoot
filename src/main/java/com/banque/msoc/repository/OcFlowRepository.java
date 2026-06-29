package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.enums.OcFlowStatus;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OcFlowRepository extends JpaRepository<OcFlow, Long>, JpaSpecificationExecutor<OcFlow> {
    Optional<OcFlow> findByBusinessKey(String businessKey);

    @EntityGraph(attributePaths = "detail")
    Optional<OcFlow> findWithDetailByBusinessKey(String businessKey);

    @Query(value = """
    SELECT
        TO_CHAR(d.day_date, 'YYYY-MM-DD') AS dateKey,
        REPLACE(INITCAP(TO_CHAR(d.day_date, 'DY', 'NLS_DATE_LANGUAGE=FRENCH')), '.', '') AS jour,
        COUNT(f.CREATED_AT) AS flux
    FROM (
        SELECT TRUNC(SYSDATE) - 7 + LEVEL AS day_date
        FROM dual
        CONNECT BY LEVEL <= 7
    ) d
    LEFT JOIN OC_FLOW f
        ON TRUNC(CAST(f.CREATED_AT AS DATE)) = d.day_date
    GROUP BY d.day_date
    ORDER BY d.day_date
    """, nativeQuery = true)
    List<OcFlowActivityProjection> countFlowsByCreatedAtLast7Days();

    @EntityGraph(attributePaths = {"detail"})
    List<OcFlow> findTop10ByOrderByCreatedAtDesc();
}
