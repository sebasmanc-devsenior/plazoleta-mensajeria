package com.plazoleta.messaging.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plazoleta.messaging.application.dto.SendSmsCommand;
import com.plazoleta.messaging.application.dto.SmsResponse;
import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.exception.SmsDeliveryException;
import com.plazoleta.messaging.domain.model.SmsMessage;

/** HU-14: enviar un SMS. */
@ExtendWith(MockitoExtension.class)
class SendSmsUseCaseTest {
    @Mock
    private SmsSenderPort smsSenderPort;
    @InjectMocks
    private SendSmsUseCase service;

    @Test
    void sendsTheMessageToThePhoneThroughTheProvider() {
        when(smsSenderPort.send(any())).thenReturn("SM123");
        when(smsSenderPort.providerName()).thenReturn("twilio");

        SmsResponse response = service.execute(new SendSmsCommand("+573001112233", "Tu pedido esta listo. PIN: 123456"));

        ArgumentCaptor<SmsMessage> sent = ArgumentCaptor.forClass(SmsMessage.class);
        verify(smsSenderPort).send(sent.capture());
        assertThat(sent.getValue().getPhone()).isEqualTo("+573001112233");
        assertThat(sent.getValue().getText()).isEqualTo("Tu pedido esta listo. PIN: 123456");
        assertThat(response.getPhone()).isEqualTo("+573001112233");
        assertThat(response.getProvider()).isEqualTo("twilio");
        assertThat(response.getProviderMessageId()).isEqualTo("SM123");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "abc", "123", "+57300111223344556"})
    void rejectsAnInvalidPhone(String phone) {
        assertThatThrownBy(() -> service.execute(new SendSmsCommand(phone, "hola")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(smsSenderPort, never()).send(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void rejectsAnEmptyMessage(String message) {
        assertThatThrownBy(() -> service.execute(new SendSmsCommand("+573001112233", message)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(smsSenderPort, never()).send(any());
    }

    @Test
    void rejectsAMessageThatIsTooLong() {
        String tooLong = "a".repeat(SmsMessage.MAX_LENGTH + 1);

        assertThatThrownBy(() -> service.execute(new SendSmsCommand("+573001112233", tooLong)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAMissingBody() {
        assertThatThrownBy(() -> service.execute(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void letsTheProviderFailureReachTheCaller() {
        when(smsSenderPort.send(any())).thenThrow(new SmsDeliveryException("Twilio no está disponible"));

        assertThatThrownBy(() -> service.execute(new SendSmsCommand("+573001112233", "hola")))
                .isInstanceOf(SmsDeliveryException.class);
    }
}
