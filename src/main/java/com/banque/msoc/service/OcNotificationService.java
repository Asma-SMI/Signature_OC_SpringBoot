package com.banque.msoc.service;

import com.banque.msoc.dto.notification.OcNotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class OcNotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        log.info("[MS-OC SSE] Nouveau client connecté. Clients actifs: {}", emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("[MS-OC SSE] Client déconnecté. Clients actifs: {}", emitters.size());
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info("[MS-OC SSE] Timeout client. Clients actifs: {}", emitters.size());

            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });

        emitter.onError(error -> {
            emitters.remove(emitter);
            log.warn("[MS-OC SSE] Erreur client SSE. Clients actifs: {}. Erreur: {}",
                    emitters.size(),
                    error.getMessage()
            );
        });

        try {
            emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name("CONNECTED")
                    .data("Connexion notifications MS-OC ouverte - " + LocalDateTime.now(), MediaType.TEXT_PLAIN));

            log.info("[MS-OC SSE] Event CONNECTED envoyé au client SSE.");
        } catch (Exception e) {
            log.warn("[MS-OC SSE] Impossible d'envoyer CONNECTED: {}", e.getMessage());
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void sendNewFlowNotification(OcNotificationDto notification) {
        if (notification == null) {
            log.warn("[MS-OC SSE] Notification null, aucun envoi.");
            return;
        }

        log.info(
                "[MS-OC SSE] Envoi notification temps réel. id={}, businessKey={}, dossier={}, clients={}",
                notification.id(),
                notification.businessKey(),
                notification.numeroDossier(),
                emitters.size()
        );

        if (emitters.isEmpty()) {
            log.warn("[MS-OC SSE] Aucun client connecté. Notification sauvegardée uniquement en BDD.");
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                /*
                 * Important :
                 * Pas de .name("OC_NEW_FLOW")
                 * Comme ça, React reçoit directement dans eventSource.onmessage.
                 */
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .data(notification, MediaType.APPLICATION_JSON));

            } catch (Exception e) {
                emitters.remove(emitter);
                log.warn("[MS-OC SSE] Client SSE déconnecté, suppression de l'emitter: {}", e.getMessage());
            }
        }
    }
}