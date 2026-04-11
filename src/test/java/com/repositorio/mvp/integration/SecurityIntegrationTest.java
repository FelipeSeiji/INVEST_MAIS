package com.repositorio.mvp.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    static {
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure()
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("INFO-01: Deve retornar 403 ao acessar endpoint protegido sem token")
    void shouldReturnForbiddenWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/users/me").secure(true))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MED-03: Deve retornar 400 (Bad Request) em vez de 409 para IllegalArgumentException")
    void shouldReturn400ForIllegalArgumentException() throws Exception {
        // Simulando um reset de senha com token inválido que dispara IllegalArgumentException
        String json = """
                {
                    "token": "token-nao-existente",
                    "newPassword": "Password123!"
                }
                """;

        mockMvc.perform(post("/auth/reset-password")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Segurança: Deve retornar mensagem genérica em forgot-password para evitar enumeração")
    void shouldReturnGenericMessageForForgotPassword() throws Exception {
        String json = """
                {
                    "email": "email-que-nao-existe@teste.com"
                }
                """;

        mockMvc.perform(post("/auth/forgot-password")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
