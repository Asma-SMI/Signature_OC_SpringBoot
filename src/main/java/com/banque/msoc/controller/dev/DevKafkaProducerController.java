package com.banque.msoc.controller.dev;

import com.banque.msoc.dto.kafka.OcInboundKafkaMessage;
import com.banque.msoc.dto.kafka.station.StationOcInboundDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/oc")
@Profile("dev")
@RequiredArgsConstructor
public class DevKafkaProducerController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${msoc.kafka.inbound-topic}")
    private String inboundTopic;


    @PostMapping("/dev/kafka/inbound")
    public ResponseEntity<String> publishInbound(@RequestBody StationOcInboundDto message) throws JsonProcessingException {

        String messageId = message.getHeader() != null
                ? message.getHeader().getMessageId()
                : null;

        String json = objectMapper.writeValueAsString(message);

        kafkaTemplate.send(
                inboundTopic,
                messageId,
                json
        );

        return ResponseEntity.ok(
                "Message publie vers Kafka topic: " + inboundTopic
        );
    }
}
