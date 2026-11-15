package com.plazoleta.messaging.application.handler;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.input.SendSmsUseCase;

/** Delega en el caso de uso HU-14. Se registra como @Bean en BeanConfiguration (sin Spring aquí). */
public class SmsHandler implements ISmsHandler {
    private final SendSmsUseCase sendSmsUseCase;

    public SmsHandler(SendSmsUseCase sendSmsUseCase) {
        this.sendSmsUseCase = sendSmsUseCase;
    }

    @Override
    public SmsResponse sendSms(SendSmsCommand command) {
        return sendSmsUseCase.execute(command);
    }
}
