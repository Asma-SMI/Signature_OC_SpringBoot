package com.banque.msoc.kafka;

import com.banque.msoc.dto.kafka.OcInboundKafkaMessage;
import com.banque.msoc.service.OcInboundService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OcInboundConsumer {
    private final OcInboundService inboundService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${msoc.kafka.inbound-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset) throws Exception {
        OcInboundKafkaMessage dto = objectMapper.readValue(message, OcInboundKafkaMessage.class);
        inboundService.processInboundMessage(dto, topic, partition, offset);
    }
}
