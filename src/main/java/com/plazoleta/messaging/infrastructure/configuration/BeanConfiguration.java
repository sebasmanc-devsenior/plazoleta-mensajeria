package com.plazoleta.messaging.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.plazoleta.messaging.application.port.input.SendSmsUseCase;
import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.application.service.SendSmsService;
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
                                       @Value("${app.sms.twilio.from-number}") String fromNumber) {
        if ("twilio".equalsIgnoreCase(provider)) {
            return new TwilioSmsAdapter(twilioBaseUrl, accountSid, authToken, fromNumber);
        }
        if ("simulated".equalsIgnoreCase(provider)) {
            return new SimulatedSmsAdapter();
        }
        throw new IllegalStateException("SMS_PROVIDER must be 'simulated' or 'twilio'");
    }

    /** HU-14 */
    @Bean
    public SendSmsUseCase sendSmsUseCase(SmsSenderPort smsSenderPort) {
        return new SendSmsService(smsSenderPort);
    }
}
