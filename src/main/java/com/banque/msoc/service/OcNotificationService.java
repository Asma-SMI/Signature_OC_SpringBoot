package com.banque.msoc.service;

import com.banque.msoc.dto.notification.OcNotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class OcNotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> {
            log.debug("SSE completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE timeout");
            emitters.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
                // Ignore volontairement : le client peut déjà être fermé.
            }
        });

        emitter.onError(error -> {
            log.debug("SSE error/client disconnected: {}", error.getMessage());
            emitters.remove(emitter);
            // Ne pas appeler emitter.complete() ici.
        });

        emitters.add(emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data("Connexion notifications MS-OC ouverte"));
        } catch (Exception e) {
            log.debug("Impossible d'envoyer CONNECTED, client déjà déconnecté: {}", e.getMessage());
            emitters.remove(emitter);
            // Ne pas appeler complete ici non plus.
        }

        return emitter;
    }

    public void sendNewFlowNotification(OcNotificationDto notification) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("OC_NEW_FLOW")
                        .data(notification, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                log.debug("Client SSE déconnecté, suppression de l'emitter: {}", e.getMessage());
                emitters.remove(emitter);

                // Très important :
                // Ne PAS faire emitter.complete() ici.
                // Sinon Tomcat peut déclencher :
                // A non-container thread attempted to use the AsyncContext after an error...
            }
        }
    }
}