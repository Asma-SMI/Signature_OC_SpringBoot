package com.banque.msoc.service;

import com.banque.msoc.domain.entity.OcNotification;
import com.banque.msoc.dto.notification.OcNewFlowReceivedEvent;
import com.banque.msoc.dto.notification.OcNotificationDto;
import com.banque.msoc.repository.OcNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class OcNotificationPersistenceService {
    private final OcNotificationRepository notificationRepository;
    private final OcNotificationService sseService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OcNotificationDto createNewFlowNotification(OcNewFlowReceivedEvent event) {

        if (event.sourceEventId() != null &&
                notificationRepository.existsBySourceEventId(event.sourceEventId())) {
            return null;
        }

        boolean signatureInvalid = "INVALID".equalsIgnoreCase(event.signatureStatus());

        OcNotification notification = OcNotification.builder()
                .eventType("OC_NEW_FLOW")
                .type(signatureInvalid ? "error" : "info")
                .category(signatureInvalid ? "alerte" : "flux_oc")
                .title(signatureInvalid
                        ? "Flux OC reçu avec signature invalide"
                        : "Nouveau flux OC reçu")
                .message(signatureInvalid
                        ? "Le flux OC " + event.numeroDossier() + " a été reçu avec une signature invalide."
                        : "Dossier " + event.numeroDossier() + " reçu depuis la station d'échange. Demande TTN : " + event.numeroDemande() + ".")
                .businessKey(event.businessKey())
                .numeroDossier(event.numeroDossier())
                .numeroDemande(event.numeroDemande())
                .statutMetier(event.statutMetier())
                .signatureStatus(event.signatureStatus())
                .readFlag("N")
                .sourceEventId(event.sourceEventId())
                .correlationId(event.correlationId())
                .createdAt(event.createdAt() != null ? event.createdAt() : LocalDateTime.now())
                .build();

        OcNotification saved = notificationRepository.save(notification);

        OcNotificationDto dto = toDto(saved);

        try {
            sseService.sendNewFlowNotification(dto);
        } catch (Exception e) {
            log.debug("Notification sauvegardée en BDD mais non envoyée en SSE: {}", e.getMessage());
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<OcNotificationDto> findNotifications(Boolean unreadOnly, Pageable pageable) {
        Page<OcNotification> page = Boolean.TRUE.equals(unreadOnly)
                ? notificationRepository.findByReadFlagOrderByCreatedAtDesc("N", pageable)
                : notificationRepository.findAllByOrderByCreatedAtDesc(pageable);

        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long countUnread() {
        return notificationRepository.countByReadFlag("N");
    }

    @Transactional
    public void markAsRead(Long id) {
        OcNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification introuvable: " + id));

        notification.setReadFlag("Y");
        notification.setReadAt(LocalDateTime.now());
    }

    @Transactional
    public void markAllAsRead() {
        Page<OcNotification> unread = notificationRepository.findByReadFlagOrderByCreatedAtDesc(
                "N",
                PageRequest.of(0, 500)
        );

        unread.forEach(n -> {
            n.setReadFlag("Y");
            n.setReadAt(LocalDateTime.now());
        });
    }

    private OcNotificationDto toDto(OcNotification n) {
        return new OcNotificationDto(
                n.getId(),
                n.getType(),
                n.getCategory(),
                n.getEventType(),
                n.getBusinessKey(),
                n.getNumeroDossier(),
                n.getNumeroDemande(),
                n.getStatutMetier(),
                n.getSignatureStatus(),
                null,
                n.getCreatedAt(),
                n.getTitle(),
                n.getMessage(),
                "Y".equalsIgnoreCase(n.getReadFlag())
        );
    }
}
