package com.plazoleta.messaging.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.input.SendSmsPort;

/** El handler no decide nada: pasa el comando al caso de uso y devuelve su respuesta. */
class SmsHandlerTest {
    private final SendSmsPort sendSmsUseCase = mock(SendSmsPort.class);
    private final SmsHandler handler = new SmsHandler(sendSmsUseCase);

    @Test
    void sendSmsDelegatesToTheUseCase() {
        SendSmsCommand command = new SendSmsCommand("+573001112233", "Hola");
        SmsResponse expected = new SmsResponse("+573001112233", "simulated", "SIM-1");
        when(sendSmsUseCase.execute(command)).thenReturn(expected);

        SmsResponse response = handler.sendSms(command);

        assertThat(response).isSameAs(expected);
        verify(sendSmsUseCase).execute(command);
    }
}
