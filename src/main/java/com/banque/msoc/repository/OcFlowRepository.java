package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcFlow;
import com.banque.msoc.domain.enums.OcFlowStatus;
import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OcFlowRepository extends JpaRepository<OcFlow, Long>, JpaSpecificationExecutor<OcFlow> {
    Optional<OcFlow> findByBusinessKey(String businessKey);

    @EntityGraph(attributePaths = "detail")
    Optional<OcFlow> findWithDetailByBusinessKey(String businessKey);

    long countByStatus(OcFlowStatus status);

}
