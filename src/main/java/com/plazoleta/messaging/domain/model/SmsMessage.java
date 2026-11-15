package com.plazoleta.messaging.domain.model;

import com.plazoleta.messaging.domain.util.MensajesError;

/** Mensaje de texto (SMS) para un teléfono. Se valida al crearse. */
public class SmsMessage {
    /** Un SMS admite 160 caracteres; se permiten hasta dos segmentos. */
    public static final int MAX_LENGTH = 320;

    private final String phone;
    private final String text;

    public SmsMessage(String phone, String text) {
        if (phone == null || !phone.matches("^\\+?\\d{7,13}$")) {
            throw new IllegalArgumentException(MensajesError.TELEFONO_DEBE_TENER_DE_7_A_13);
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(MensajesError.MENSAJE_ES_OBLIGATORIO);
        }
        if (text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format(MensajesError.MENSAJE_DEMASIADO_LARGO, MAX_LENGTH));
        }
        this.phone = phone;
        this.text = text;
    }

    public String getPhone() { return phone; }
    public String getText() { return text; }
}
