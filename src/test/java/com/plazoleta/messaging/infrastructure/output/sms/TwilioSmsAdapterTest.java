package com.plazoleta.messaging.infrastructure.output.sms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.plazoleta.messaging.domain.exception.SmsDeliveryException;
import com.plazoleta.messaging.domain.model.SmsMessage;
import com.sun.net.httpserver.HttpServer;

/** HU-14: el adaptador habla con Twilio como lo pide su API (aquí, un servidor HTTP de prueba hace de Twilio). */
class TwilioSmsAdapterTest {
    private HttpServer twilio;
    private TwilioSmsAdapter adapter;
    private final AtomicReference<String> path = new AtomicReference<>();
    private final AtomicReference<String> authorization = new AtomicReference<>();
    private final AtomicReference<String> form = new AtomicReference<>();

    @BeforeEach
    void startFakeTwilio() throws IOException {
        twilio = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        twilio.start();
        adapter = new TwilioSmsAdapter("http://127.0.0.1:" + twilio.getAddress().getPort(), "AC123", "secret",
                "+15005550006");
    }

    @AfterEach
    void stopFakeTwilio() {
        twilio.stop(0);
    }

    private void twilioAnswers(int status, String json) {
        twilio.createContext("/", exchange -> {
            path.set(exchange.getRequestURI().getPath());
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
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
    void postsTheMessageToTwilioAndReturnsItsId() {
        twilioAnswers(201, "{\"sid\":\"SM999\",\"status\":\"queued\"}");

        String id = adapter.send(new SmsMessage("573001112233", "Tu pedido esta listo. PIN: 123456"));

        assertThat(id).isEqualTo("SM999");
        assertThat(path.get()).isEqualTo("/2010-04-01/Accounts/AC123/Messages.json");
        assertThat(authorization.get()).isEqualTo("Basic "
                + Base64.getEncoder().encodeToString("AC123:secret".getBytes(StandardCharsets.UTF_8)));
        assertThat(form.get()).contains("To=+573001112233").contains("From=+15005550006")
                .contains("Body=Tu pedido esta listo. PIN: 123456");
    }

    @Test
    void reportsAFailureWhenTwilioRejectsTheMessage() {
        twilioAnswers(400, "{\"code\":21211,\"message\":\"Invalid 'To' Phone Number\"}");

        assertThatThrownBy(() -> adapter.send(new SmsMessage("+573001112233", "hola")))
                .isInstanceOf(SmsDeliveryException.class).hasMessageContaining("400");
    }

    @Test
    void reportsAFailureWhenTwilioDoesNotAnswer() {
        twilio.stop(0);

        assertThatThrownBy(() -> adapter.send(new SmsMessage("+573001112233", "hola")))
                .isInstanceOf(SmsDeliveryException.class);
    }

    @Test
    void needsTheTwilioCredentials() {
        assertThatThrownBy(() -> new TwilioSmsAdapter("http://localhost:1", "", "secret", "+15005550006"))
                .isInstanceOf(IllegalStateException.class);
    }
}
