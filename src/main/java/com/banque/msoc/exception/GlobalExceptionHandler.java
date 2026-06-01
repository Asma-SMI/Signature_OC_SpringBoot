package com.banque.msoc.exception;

import com.banque.msoc.dto.rest.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FlowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FlowNotFoundException ex, HttpServletRequest request) {
        log.warn("[MS-OC] Flow not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "BUSINESS_ERROR", ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler({InvalidDecisionException.class, DuplicateMessageException.class, InvalidStatusTransitionException.class, OperationAlreadyFinalizedException.class})
    public ResponseEntity<ErrorResponse> handleBusiness(MsOcException ex, HttpServletRequest request) {
        log.warn("[MS-OC] Business error: {} — {}", ex.getErrorCode(), ex.getMessage());
        HttpStatus status = ex instanceof InvalidDecisionException ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
        return build(status, "BUSINESS_ERROR", ex.getErrorCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("[MS-OC] Validation error: {}", message);
        return build(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", "OC_ERR_008", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("[MS-OC] Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "TECHNICAL_ERROR", "OC_SYS_ERR_002", ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String type, String code, String message, HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorType(type)
                .errorCode(code)
                .message(message)
                .correlationId(correlationId)
                .build());
    }
}
