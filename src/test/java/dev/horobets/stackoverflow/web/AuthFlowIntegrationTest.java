package dev.horobets.stackoverflow.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void register_login_and_get_me_success() throws Exception {
        // Register
        String registerPayload = "{" +
                "\"username\":\"alice\"," +
                "\"email\":\"a@ex.com\"," +
                "\"password\":\"secret123\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("alice"));

        // Login
        String loginPayload = "{" +
                "\"username\":\"alice\"," +
                "\"password\":\"secret123\"" +
                "}";
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String tokenType = loginJson.get("tokenType").asText();
        String token = loginJson.get("token").asText();
        String authHeader = tokenType + " " + token;
        assertThat(token).isNotBlank();

        // /me
        String meResponse = mockMvc.perform(get("/api/me")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("a@ex.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode meJson = objectMapper.readTree(meResponse);
        assertThat(meJson.get("reputation").asInt()).isGreaterThanOrEqualTo(1);
        assertThat(meJson.get("roles")).isNotNull();
        assertThat(meJson.get("createdAt").asText()).isNotBlank();
    }

    @Test
    void me_requires_auth() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }
}

