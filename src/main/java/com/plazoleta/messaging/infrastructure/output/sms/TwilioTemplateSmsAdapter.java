package com.plazoleta.messaging.infrastructure.output.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.model.SmsMessage;
import com.plazoleta.messaging.domain.util.MensajesError;

/**
 * Proveedor para cuentas Trial de Twilio, que solo envían plantillas predefinidas: al celular se envía la
 * plantilla (por ejemplo "sms_2fa") para demostrar que la integración real funciona, y el mensaje verdadero,
 * con el PIN del pedido, se escribe en la consola. Si Twilio falla, no se escribe nada y el envío falla (502).
 */
public class TwilioTemplateSmsAdapter implements SmsSenderPort {
    private static final Logger log = LoggerFactory.getLogger(TwilioTemplateSmsAdapter.class);

    private final TwilioSmsAdapter twilio;
    private final String template;

    public TwilioTemplateSmsAdapter(TwilioSmsAdapter twilio, String template) {
        if (template == null || template.isBlank()) {
            throw new IllegalStateException(MensajesError.SMS_PROVIDER_TWILIO_PLANTILLA_NECESITA_TWILIO_TEMPLATE);
        }
        this.twilio = twilio;
        this.template = template;
    }

    @Override
    public String providerName() {
        return "twilio-plantilla";
    }

    @Override
    public String send(SmsMessage message) {
        String twilioId = twilio.send(new SmsMessage(message.getPhone(), template));
        log.info("[SMS TWILIO PLANTILLA] id={} para={} plantilla enviada al celular=\"{}\" mensaje real=\"{}\"",
                twilioId, message.getPhone(), template, message.getText());
        return twilioId;
    }
}
