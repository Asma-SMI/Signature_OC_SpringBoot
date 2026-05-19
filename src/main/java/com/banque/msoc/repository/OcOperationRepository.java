package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcOperation;
import com.banque.msoc.domain.enums.OcOperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OcOperationRepository extends JpaRepository<OcOperation, Long> {
    Optional<OcOperation> findFirstByBusinessKeyOrderByCreatedAtDesc(String businessKey);
    Optional<OcOperation> findFirstByBusinessKeyAndStatusInOrderByCreatedAtDesc(String businessKey, Iterable<OcOperationStatus> statuses);
}
