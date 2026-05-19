package com.banque.msoc.dto.rest;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String errorType;
    private String errorCode;
    private String message;
    private String correlationId;
}
