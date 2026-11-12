package com.plazoleta.messaging.infrastructure.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** HU-14: la ruta interna de SMS, con el proveedor simulado (configuración de pruebas). */
@SpringBootTest
@AutoConfigureMockMvc
class InternalSmsControllerTests {
    private static final String BODY = "{\"phone\":\"+573001112233\",\"message\":\"Tu pedido esta listo. PIN: 123456\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendsTheSmsWithTheInternalToken() throws Exception {
        mockMvc.perform(post("/internal/sms").header("Authorization", "Bearer test-internal-token")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+573001112233"))
                .andExpect(jsonPath("$.provider").value("simulated"))
                .andExpect(jsonPath("$.providerMessageId").isNotEmpty());
    }

    @Test
    void rejectsACallWithoutTheInternalToken() throws Exception {
        mockMvc.perform(post("/internal/sms").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsACallWithAWrongToken() throws Exception {
        mockMvc.perform(post("/internal/sms").header("Authorization", "Bearer otro-token")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsAnInvalidPhone() throws Exception {
        mockMvc.perform(post("/internal/sms").header("Authorization", "Bearer test-internal-token")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"phone\":\"abc\",\"message\":\"hola\"}"))
                .andExpect(status().isBadRequest());
    }
}
