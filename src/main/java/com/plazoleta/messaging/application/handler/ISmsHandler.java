package com.plazoleta.messaging.application.handler;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;

/**
 * Handler de SMS: lo que el controlador le pide a la capa de aplicación. No tiene reglas de negocio,
 * solo coordina y delega en el caso de uso por su interfaz (puerto de entrada).
 */
public interface ISmsHandler {
    SmsResponse sendSms(SendSmsCommand command);
}
