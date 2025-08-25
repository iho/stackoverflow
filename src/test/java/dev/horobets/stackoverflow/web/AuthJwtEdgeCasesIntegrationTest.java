package dev.horobets.stackoverflow.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthJwtEdgeCasesIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private static Key testKey() {
        // default secret from JwtService @Value fallback
        String base64 = "ZmFrZV9zdXBlcl9zZWNyZXRfYmFzZTY0X2tleV9zaG91bGRfYmVfMzJfYnl0ZXM=";
        byte[] keyBytes = Decoders.BASE64.decode(base64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void invalid_token_returns_401() throws Exception {
        mockMvc.perform(get("/api/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expired_token_returns_401() throws Exception {
        // ensure user exists (not strictly needed since parsing will fail before lookup)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"eve\",\"email\":\"e@ex.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk());

        Instant now = Instant.now();
        String expired = Jwts.builder()
                .setSubject("eve")
                .setIssuedAt(Date.from(now.minusSeconds(3600)))
                .setExpiration(Date.from(now.minusSeconds(60)))
                .signWith(testKey(), SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(get("/api/me").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }
}

