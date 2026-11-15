package com.plazoleta.messaging.domain.util;

/**
 * Mensajes de error del microservicio, en un solo lugar. Se usan en el dominio, la aplicación y la
 * infraestructura (validaciones, excepciones y manejador de errores). Clase de Java puro, sin Spring.
 */
public final class MensajesError {
    public static final String CREDENCIALES_DE_SERVICIO_INVALIDAS = "Credenciales de servicio inválidas";
    public static final String CUERPO_DE_LA_PETICION_ES_OBLIGATORIO = "El cuerpo de la petición es obligatorio";
    public static final String MENSAJE_DEMASIADO_LARGO = "El mensaje puede tener máximo %d caracteres";
    public static final String MENSAJE_ES_OBLIGATORIO = "El mensaje es obligatorio";
    public static final String PETICION_ESTA_MAL_FORMADA = "La petición está mal formada";
    public static final String SMS_PROVIDER_DEBE_SER_SIMULATED_O_TWILIO = "SMS_PROVIDER debe ser 'simulated' o 'twilio'";
    public static final String SMS_PROVIDER_TWILIO_NECESITA_TWILIO_ACCOUNT_SID = "SMS_PROVIDER=twilio necesita TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN y TWILIO_FROM_NUMBER";
    public static final String TELEFONO_DEBE_TENER_DE_7_A_13 = "El teléfono debe tener de 7 a 13 dígitos y puede empezar con +";
    public static final String TWILIO_NO_CONFIRMO_EL_MENSAJE = "Twilio no confirmó el mensaje";
    public static final String TWILIO_NO_ESTA_DISPONIBLE = "Twilio no está disponible";
    public static final String TWILIO_RECHAZO_EL_MENSAJE_HTTP = "Twilio rechazó el mensaje (HTTP ";
    public static final String ERROR_INTERNO_DEL_SERVIDOR = "Error interno del servidor";
    public static final String RUTA_NO_EXISTE = "La ruta solicitada no existe";
    public static final String PETICION_NO_SOPORTADA = "La petición no se puede procesar con ese método o formato";

    private MensajesError() {
    }
}
