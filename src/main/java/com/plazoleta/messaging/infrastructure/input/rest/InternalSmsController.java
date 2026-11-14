package com.plazoleta.messaging.infrastructure.input.rest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.input.SendSmsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Ruta entre microservicios: solo la puede llamar otro servicio con el token interno. */
@RestController
@RequestMapping("/internal/sms")
@Tag(name = "SMS")
public class InternalSmsController {
    private final SendSmsUseCase sendSmsUseCase;
    private final String internalToken;

    public InternalSmsController(SendSmsUseCase sendSmsUseCase,
                                 @Value("${app.internal.token}") String internalToken) {
        this.sendSmsUseCase = sendSmsUseCase;
        this.internalToken = internalToken;
    }

    @PostMapping
    @Operation(summary = "HU-14 Send SMS",
            description = "Internal route. Needs the header Authorization: Bearer <internal token>.")
    public SmsResponse sendSms(@RequestBody SendSmsCommand command,
                               @RequestHeader(name = "Authorization", required = false) String authorization) {
        String supplied = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7) : "";
        if (internalToken == null || internalToken.isBlank()
                || !MessageDigest.isEqual(internalToken.getBytes(StandardCharsets.UTF_8),
                        supplied.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidServiceCredentialsException("Credenciales de servicio inválidas");
        }
        return sendSmsUseCase.execute(command);
    }

    public static class InvalidServiceCredentialsException extends RuntimeException {
        public InvalidServiceCredentialsException(String message) {
            super(message);
        }
    }
}
