package com.plazoleta.messaging.domain.model;

/** Mensaje de texto (SMS) para un teléfono. Se valida al crearse. */
public class SmsMessage {
    /** Un SMS admite 160 caracteres; se permiten hasta dos segmentos. */
    public static final int MAX_LENGTH = 320;

    private final String phone;
    private final String text;

    public SmsMessage(String phone, String text) {
        if (phone == null || !phone.matches("^\\+?\\d{7,13}$")) {
            throw new IllegalArgumentException("El teléfono debe tener de 7 a 13 dígitos y puede empezar con +");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        if (text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("El mensaje puede tener máximo " + MAX_LENGTH + " caracteres");
        }
        this.phone = phone;
        this.text = text;
    }

    public String getPhone() { return phone; }
    public String getText() { return text; }
}
