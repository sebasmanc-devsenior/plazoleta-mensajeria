package com.plazoleta.messaging.infrastructure.input.rest.exceptionhandler;

import com.plazoleta.messaging.domain.util.MensajesError;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.plazoleta.messaging.domain.exception.SmsDeliveryException;
import com.plazoleta.messaging.infrastructure.input.rest.InternalSmsController.InvalidServiceCredentialsException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformed(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, MensajesError.PETICION_ESTA_MAL_FORMADA, request);
    }

    @ExceptionHandler(InvalidServiceCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(InvalidServiceCredentialsException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(SmsDeliveryException.class)
    public ResponseEntity<ErrorResponse> handleDelivery(SmsDeliveryException ex, HttpServletRequest request) {
        log.warn("SMS delivery failed: {}", ex.getMessage(), ex.getCause());
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), request);
    }

    /** Error no previsto: 500 con el formato del proyecto y sin detalles internos (el detalle queda en el log). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        if (ex instanceof org.springframework.web.ErrorResponse frameworkError) {
            HttpStatus status = HttpStatus.valueOf(frameworkError.getStatusCode().value());
            return build(status, status == HttpStatus.NOT_FOUND ? MensajesError.RUTA_NO_EXISTE
                    : MensajesError.PETICION_NO_SOPORTADA, request);
        }
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, MensajesError.ERROR_INTERNO_DEL_SERVIDOR, request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), message, request.getRequestURI()));
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
    }
}
