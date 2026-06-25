package com.banque.msoc.repository;

import com.banque.msoc.domain.entity.OcNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcNotificationRepository extends JpaRepository<OcNotification, Long> {

    Page<OcNotification> findByReadFlagOrderByCreatedAtDesc(String readFlag, Pageable pageable);

    Page<OcNotification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByReadFlag(String readFlag);

    boolean existsBySourceEventId(String sourceEventId);
}