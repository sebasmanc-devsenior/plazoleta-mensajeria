package com.plazoleta.messaging.application.dto;

/** Resultado de enviar un SMS: a qué teléfono, con qué proveedor y el id que devolvió. */
public class SmsResponse {
    private final String phone;
    private final String provider;
    private final String providerMessageId;

    public SmsResponse(String phone, String provider, String providerMessageId) {
        this.phone = phone;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
    }

    public String getPhone() { return phone; }
    public String getProvider() { return provider; }
    public String getProviderMessageId() { return providerMessageId; }
}
