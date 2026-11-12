package com.plazoleta.messaging.application.port.output;

import com.plazoleta.messaging.domain.model.SmsMessage;

/** Proveedor que entrega el SMS (Twilio de verdad, o uno simulado que solo lo escribe en consola). */
public interface SmsSenderPort {
    /** Nombre del proveedor, para informarlo en la respuesta. */
    String providerName();

    /** Entrega el mensaje y devuelve el id que le asignó el proveedor. */
    String send(SmsMessage message);
}
