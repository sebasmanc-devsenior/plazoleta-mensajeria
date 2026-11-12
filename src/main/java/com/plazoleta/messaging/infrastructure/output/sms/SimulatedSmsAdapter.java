package com.plazoleta.messaging.infrastructure.output.sms;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.model.SmsMessage;

/** Proveedor simulado: no envía nada, escribe el SMS en la consola. Sirve para demostrar sin cuenta de Twilio. */
public class SimulatedSmsAdapter implements SmsSenderPort {
    private static final Logger log = LoggerFactory.getLogger(SimulatedSmsAdapter.class);

    @Override
    public String providerName() {
        return "simulated";
    }

    @Override
    public String send(SmsMessage message) {
        String id = "SIM-" + UUID.randomUUID();
        log.info("[SMS SIMULADO] id={} para={} mensaje=\"{}\"", id, message.getPhone(), message.getText());
        return id;
    }
}
