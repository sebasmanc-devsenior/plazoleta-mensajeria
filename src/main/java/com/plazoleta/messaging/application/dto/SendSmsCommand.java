package com.plazoleta.messaging.application.dto;

/** Datos de entrada para enviar un SMS. */
public class SendSmsCommand {
    private String phone;
    private String message;

    public SendSmsCommand() {
    }

    public SendSmsCommand(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
