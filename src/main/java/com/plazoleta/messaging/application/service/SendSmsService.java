package com.plazoleta.messaging.application.service;

import com.plazoleta.messaging.domain.util.MensajesError;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.input.SendSmsUseCase;
import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.model.SmsMessage;

public class SendSmsService implements SendSmsUseCase {
    private final SmsSenderPort smsSenderPort;

    public SendSmsService(SmsSenderPort smsSenderPort) {
        this.smsSenderPort = smsSenderPort;
    }

    @Override
    public SmsResponse execute(SendSmsCommand command) {
        if (command == null) {
            throw new IllegalArgumentException(MensajesError.CUERPO_DE_LA_PETICION_ES_OBLIGATORIO);
        }
        SmsMessage message = new SmsMessage(command.getPhone(), command.getMessage());
        String providerMessageId = smsSenderPort.send(message);
        return new SmsResponse(message.getPhone(), smsSenderPort.providerName(), providerMessageId);
    }
}
