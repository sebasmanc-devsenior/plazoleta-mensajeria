package com.plazoleta.messaging.infrastructure.input.rest.mapper;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.infrastructure.input.rest.dto.SendSmsRequest;

/** Convierte el request HTTP en el comando de la capa de aplicación. */
public final class SmsRequestMapper {
    private SmsRequestMapper() {
    }

    public static SendSmsCommand toCommand(SendSmsRequest request) {
        return request == null ? null : new SendSmsCommand(request.phone(), request.message());
    }
}
