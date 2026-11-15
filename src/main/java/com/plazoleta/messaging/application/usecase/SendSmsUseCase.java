package com.plazoleta.messaging.application.usecase;

import com.plazoleta.messaging.domain.util.MensajesError;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.input.SendSmsPort;
import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.model.SmsMessage;

public class SendSmsUseCase implements SendSmsPort {
    private final SmsSenderPort smsSenderPort;

    public SendSmsUseCase(SmsSenderPort smsSenderPort) {
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
