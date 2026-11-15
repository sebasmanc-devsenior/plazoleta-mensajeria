package com.plazoleta.messaging.application.port.input;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;

/** HU-14: enviar un mensaje SMS a un teléfono. */
public interface SendSmsPort {
    SmsResponse execute(SendSmsCommand command);
}
