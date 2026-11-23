package com.plazoleta.messaging.infrastructure.output.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.plazoleta.messaging.domain.exception.SmsDeliveryException;
import com.plazoleta.messaging.domain.model.SmsMessage;
import com.sun.net.httpserver.HttpServer;

/**
 * HU-14 con cuenta Trial de Twilio: al celular va la plantilla y el mensaje real (con el PIN) queda en la consola.
 * Un servidor HTTP de prueba hace de Twilio.
 */
@ExtendWith(OutputCaptureExtension.class)
class TwilioTemplateSmsAdapterTest {
    private static final String REAL_TEXT = "Plazoleta: tu pedido #7 está listo. PIN para reclamarlo: 654321";

    private HttpServer twilio;
    private TwilioTemplateSmsAdapter adapter;
    private final AtomicReference<String> form = new AtomicReference<>();

    @BeforeEach
    void startFakeTwilio() throws IOException {
        twilio = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        twilio.start();
        adapter = new TwilioTemplateSmsAdapter(new TwilioSmsAdapter("http://127.0.0.1:" + twilio.getAddress().getPort(),
                "AC123", "secret", "+15005550006"), "sms_2fa");
    }

    @AfterEach
    void stopFakeTwilio() {
        twilio.stop(0);
    }

    private void twilioAnswers(int status, String json) {
        twilio.createContext("/", exchange -> {
            form.set(URLDecoder.decode(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8));
            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
    }

    @Test
    void sendsTheTemplateToThePhoneAndWritesTheRealMessageInTheConsole(CapturedOutput output) {
        twilioAnswers(201, "{\"sid\":\"SM777\",\"status\":\"queued\"}");

        String id = adapter.send(new SmsMessage("+573001112233", REAL_TEXT));

        assertThat(id).isEqualTo("SM777");
        assertThat(form.get()).contains("To=+573001112233").contains("From=+15005550006").contains("Body=sms_2fa")
                .doesNotContain("654321");
        assertThat(output).contains("[SMS TWILIO PLANTILLA] id=SM777 para=+573001112233")
                .contains("plantilla enviada al celular=\"sms_2fa\"").contains(REAL_TEXT);
        assertThat(adapter.providerName()).isEqualTo("twilio-plantilla");
    }

    @Test
    void whenTwilioRejectsTheTemplateTheSendFailsAndNothingIsWritten(CapturedOutput output) {
        twilioAnswers(400, "{\"code\":21211,\"message\":\"Invalid 'To' Phone Number\"}");

        assertThatThrownBy(() -> adapter.send(new SmsMessage("+573001112233", REAL_TEXT)))
                .isInstanceOf(SmsDeliveryException.class).hasMessageContaining("400");
        assertThat(output).doesNotContain("[SMS TWILIO PLANTILLA]");
    }

    @Test
    void needsATemplate() {
        TwilioSmsAdapter twilioAdapter = new TwilioSmsAdapter("http://localhost:1", "AC123", "secret", "+15005550006");

        assertThatThrownBy(() -> new TwilioTemplateSmsAdapter(twilioAdapter, " "))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("TWILIO_TEMPLATE");
    }
}
