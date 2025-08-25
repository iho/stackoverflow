package dev.horobets.stackoverflow.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthErrorCasesIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void login_wrong_password_returns_401() throws Exception {
        // register bob
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"email\":\"b@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());
        // wrong password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_duplicate_username_conflict_409() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"charlie\",\"email\":\"c1@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());
        // duplicate username
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"charlie\",\"email\":\"c2@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_duplicate_email_conflict_409() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dave\",\"email\":\"d@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());
        // duplicate email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dave2\",\"email\":\"d@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void login_missing_fields_returns_400() throws Exception {
        // missing password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"nobody\"}"))
                .andExpect(status().isBadRequest());
    }
}

