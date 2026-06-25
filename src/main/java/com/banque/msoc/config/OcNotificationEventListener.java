package com.banque.msoc.config;

import com.banque.msoc.dto.notification.OcNewFlowReceivedEvent;
import com.banque.msoc.dto.notification.OcNotificationDto;
import com.banque.msoc.service.OcNotificationPersistenceService;
import com.banque.msoc.service.OcNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OcNotificationEventListener {
    private final OcNotificationPersistenceService notificationPersistenceService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    public void onNewFlowReceived(OcNewFlowReceivedEvent event) {
        notificationPersistenceService.createNewFlowNotification(event);
    }
}
