package com.plazoleta.messaging.domain.model;

/** Mensaje de texto (SMS) para un teléfono. Se valida al crearse. */
public class SmsMessage {
    /** Un SMS admite 160 caracteres; se permiten hasta dos segmentos. */
    public static final int MAX_LENGTH = 320;

    private final String phone;
    private final String text;

    public SmsMessage(String phone, String text) {
        if (phone == null || !phone.matches("^\\+?\\d{7,13}$")) {
            throw new IllegalArgumentException("Phone must have 7 to 13 digits and may start with +");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        if (text.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Message must have at most " + MAX_LENGTH + " characters");
        }
        this.phone = phone;
        this.text = text;
    }

    public String getPhone() { return phone; }
    public String getText() { return text; }
}
