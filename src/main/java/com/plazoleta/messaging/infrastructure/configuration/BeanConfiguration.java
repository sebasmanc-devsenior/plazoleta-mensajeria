package com.plazoleta.messaging.infrastructure.configuration;

import com.plazoleta.messaging.domain.util.MensajesError;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.messaging.application.handler.ISmsHandler;
import com.plazoleta.messaging.application.handler.SmsHandler;
import com.plazoleta.messaging.application.port.input.SendSmsPort;
import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.application.usecase.SendSmsUseCase;
import com.plazoleta.messaging.infrastructure.output.sms.SimulatedSmsAdapter;
import com.plazoleta.messaging.infrastructure.output.sms.TwilioSmsAdapter;

/** Conecta los casos de uso con sus adaptadores. Aquí se elige el proveedor de SMS. */
@Configuration
public class BeanConfiguration {

    /** HU-14: el adaptador de salida depende de SMS_PROVIDER (simulated o twilio). */
    @Bean
    public SmsSenderPort smsSenderPort(@Value("${app.sms.provider}") String provider,
                                       @Value("${app.sms.twilio.base-url}") String twilioBaseUrl,
                                       @Value("${app.sms.twilio.account-sid}") String accountSid,
                                       @Value("${app.sms.twilio.auth-token}") String authToken,
                                       @Value("${app.sms.twilio.from-number}") String fromNumber,
                                       @Value("${app.sms.twilio.connect-timeout:2s}") Duration connectTimeout,
                                       @Value("${app.sms.twilio.read-timeout:10s}") Duration readTimeout) {
        if ("twilio".equalsIgnoreCase(provider)) {
            return new TwilioSmsAdapter(twilioBaseUrl, accountSid, authToken, fromNumber, connectTimeout, readTimeout);
        }
        if ("simulated".equalsIgnoreCase(provider)) {
            return new SimulatedSmsAdapter();
        }
        throw new IllegalStateException(MensajesError.SMS_PROVIDER_DEBE_SER_SIMULATED_O_TWILIO);
    }

    /** HU-14 */
    @Bean
    public SendSmsPort sendSmsUseCase(SmsSenderPort smsSenderPort) {
        return new SendSmsUseCase(smsSenderPort);
    }

    /** Handler que usa el controlador: delega en el caso de uso. */
    @Bean
    public ISmsHandler smsHandler(SendSmsPort sendSmsUseCase) {
        return new SmsHandler(sendSmsUseCase);
    }
}
