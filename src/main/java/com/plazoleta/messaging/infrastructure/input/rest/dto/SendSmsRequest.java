package com.plazoleta.messaging.infrastructure.input.rest.dto;

/**
 * Cuerpo de POST /internal/sms. Es una ruta interna: el teléfono y el texto los valida el dominio
 * (SmsMessage) después de comprobar el token interno, para que un llamador sin token reciba 401 y no 400.
 */
public record SendSmsRequest(String phone, String message) {
}
