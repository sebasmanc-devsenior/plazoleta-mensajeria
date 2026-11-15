# Plazoleta de comidas · Microservicio de mensajería

Microservicio que envía mensajes SMS para la plazoleta de comidas. Lo usa el microservicio de
restaurantes para avisarle al cliente que su pedido está listo (HU 14).

- **Puerto:** 8082
- **Base de datos:** no tiene
- **Java 25, Spring Boot 4.1.1, arquitectura hexagonal (arquetipo del curso)**

## Historias de usuario

| HU | Qué hace | Rama |
| --- | --- | --- |
| 14 | Enviar el SMS con el PIN cuando un pedido está listo | `historia14-enviar-sms` |

## Proveedor de SMS

El proveedor se elige con la variable `SMS_PROVIDER`:

| Valor | Qué hace | Qué necesita |
| --- | --- | --- |
| `simulated` (por defecto) | Escribe el SMS en la consola del microservicio | Nada |
| `twilio` | Envía el SMS de verdad con Twilio | `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER` |

Con `simulated`, cada mensaje aparece en la consola así:

```
[SMS SIMULADO] id=SIM-... para=+573001112233 mensaje="Plazoleta: tu pedido #5 está listo. PIN para reclamarlo: 048213"
```

## Endpoint

| Método | Ruta | Quién lo llama |
| --- | --- | --- |
| POST | `/internal/sms` | Otro microservicio, con `Authorization: Bearer <token interno>` |

```json
{ "phone": "+573001112233", "message": "Plazoleta: tu pedido #5 está listo. PIN para reclamarlo: 048213" }
```

Respuestas: 200 (enviado), 400 (teléfono o mensaje inválido), 401 (token interno incorrecto),
502 (el proveedor no pudo entregar el mensaje).

## Arquitectura hexagonal (arquetipo del curso)

```
com.plazoleta.messaging
├── domain
│   ├── model        SmsMessage (valida teléfono y texto)
│   └── exception    SmsDeliveryException
├── application
│   ├── handler      ISmsHandler (+ SmsHandler)
│   ├── port/input   SendSmsUseCase
│   ├── port/output  SmsSenderPort
│   ├── service      SendSmsService
│   └── dto          SendSmsCommand, SmsResponse
└── infrastructure
    ├── input/rest   InternalSmsController, dto (SendSmsRequest), mapper, exceptionhandler
    ├── output/sms   SimulatedSmsAdapter, TwilioSmsAdapter
    └── configuration BeanConfiguration (elige el proveedor)
```

Flujo de cada petición: `Controller` (solo HTTP: token interno, request DTO y mapper a comando) →
`IXHandler` (aplicación: coordina y delega, sin reglas de negocio) → interfaz del caso de uso (`port/input`) →
`Service` (toda la lógica) → puertos de salida → adaptadores.

## Cómo ejecutarlo

1. Copia la configuración: `Copy-Item .env.example .env` y pon en `USERS_INTERNAL_TOKEN` el mismo
   valor que tienen los microservicios de usuarios y restaurantes.
2. Arranca el servicio:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Swagger: http://localhost:8082/swagger-ui.html

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas no envían SMS reales: usan el proveedor simulado y un servidor de prueba que hace de Twilio.

## Cobertura de pruebas (JaCoCo)

Al correr las pruebas se genera el reporte de cobertura:

```powershell
.\mvnw.cmd test
```

El reporte queda en `target/site/jacoco/index.html` (ábrelo en el navegador). Muestra qué porcentaje de
instrucciones y de ramas del código ejecutan las pruebas, por paquete y por clase.
