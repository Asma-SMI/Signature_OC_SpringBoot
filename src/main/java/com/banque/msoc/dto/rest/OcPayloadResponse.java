package com.banque.msoc.dto.rest;

import com.banque.msoc.domain.enums.PayloadType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OcPayloadResponse {
    private PayloadType payloadType;
    private String payloadJson;
    private String createdBy;
    private LocalDateTime createdAt;
}
