package com.banque.msoc.kafka;

import com.banque.msoc.domain.entity.OcOutboundEvent;
import com.banque.msoc.domain.enums.EventStatus;
import com.banque.msoc.domain.enums.OcFlowStatus;
import com.banque.msoc.repository.OcFlowRepository;
import com.banque.msoc.repository.OcOutboundEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OcOutboxPublisher {
    private final OcOutboundEventRepository outboundRepository;
    private final OcFlowRepository flowRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${msoc.outbox.fixed-delay-ms}")
    @Transactional
    public void publishPendingEvents() {
        for (OcOutboundEvent event : outboundRepository.findTop50ByStatusOrderByCreatedAtAsc(EventStatus.PENDING)) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageId(), event.getPayloadJson()).get();
                event.setStatus(EventStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                if (event.getFlow().getStatus() == OcFlowStatus.ACCEPTED || event.getFlow().getStatus() == OcFlowStatus.REJECTED) {
                    event.getFlow().setStatus(OcFlowStatus.RESPONSE_REQUESTED);
                    flowRepository.save(event.getFlow());
                }
            } catch (Exception e) {
                event.setStatus(EventStatus.FAILED);
                event.setRetryCount(event.getRetryCount() == null ? 1 : event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
            }
            outboundRepository.save(event);
        }
    }
}
