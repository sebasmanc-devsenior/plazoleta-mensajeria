package com.plazoleta.messaging.domain.exception;

/** El proveedor de SMS no pudo entregar el mensaje (HTTP 502). */
public class SmsDeliveryException extends RuntimeException {
    public SmsDeliveryException(String message) {
        super(message);
    }

    public SmsDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
