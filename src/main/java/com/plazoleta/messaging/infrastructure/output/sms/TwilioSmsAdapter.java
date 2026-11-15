package com.plazoleta.messaging.infrastructure.output.sms;

import com.plazoleta.messaging.domain.util.MensajesError;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.plazoleta.messaging.application.port.output.SmsSenderPort;
import com.plazoleta.messaging.domain.exception.SmsDeliveryException;
import com.plazoleta.messaging.domain.model.SmsMessage;

/**
 * Proveedor real: envía el SMS con la API de Twilio
 * (POST /2010-04-01/Accounts/{sid}/Messages.json, autenticación básica con SID y token).
 */
public class TwilioSmsAdapter implements SmsSenderPort {
    private final RestClient client;
    private final String accountSid;
    private final String fromNumber;

    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(2);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);

    public TwilioSmsAdapter(String baseUrl, String accountSid, String authToken, String fromNumber) {
        this(baseUrl, accountSid, authToken, fromNumber, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /** Con tiempo máximo para conectar y para que Twilio responda: si Twilio se cuelga, el envío falla (502) a tiempo. */
    public TwilioSmsAdapter(String baseUrl, String accountSid, String authToken, String fromNumber,
                            Duration connectTimeout, Duration readTimeout) {
        if (isBlank(accountSid) || isBlank(authToken) || isBlank(fromNumber)) {
            throw new IllegalStateException(
                    MensajesError.SMS_PROVIDER_TWILIO_NECESITA_TWILIO_ACCOUNT_SID);
        }
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory)
                .defaultHeaders(headers -> headers.setBasicAuth(accountSid, authToken)).build();
        this.accountSid = accountSid;
        this.fromNumber = fromNumber;
    }

    @Override
    public String providerName() {
        return "twilio";
    }

    @Override
    public String send(SmsMessage message) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        // Twilio exige el número en formato internacional, con + y código de país.
        form.add("To", message.getPhone().startsWith("+") ? message.getPhone() : "+" + message.getPhone());
        form.add("From", fromNumber);
        form.add("Body", message.getText());
        try {
            Map<?, ?> response = client.post().uri("/2010-04-01/Accounts/{sid}/Messages.json", accountSid)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form)
                    .retrieve().body(Map.class);
            Object sid = response == null ? null : response.get("sid");
            if (sid == null) {
                throw new SmsDeliveryException(MensajesError.TWILIO_NO_CONFIRMO_EL_MENSAJE);
            }
            return sid.toString();
        } catch (RestClientResponseException ex) {
            throw new SmsDeliveryException(MensajesError.TWILIO_RECHAZO_EL_MENSAJE_HTTP + ex.getStatusCode().value() + ")", ex);
        } catch (RestClientException ex) {
            throw new SmsDeliveryException(MensajesError.TWILIO_NO_ESTA_DISPONIBLE, ex);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
