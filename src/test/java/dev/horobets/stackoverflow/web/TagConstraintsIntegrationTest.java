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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TagConstraintsIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String registerAndLogin(String username, String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + username + "\"," +
                                "\"email\":\"" + email + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk());
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + username + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResp);
        return loginJson.get("tokenType").asText() + " " + loginJson.get("token").asText();
    }

    @Test
    void creating_question_with_more_than_max_tags_returns_400() throws Exception {
        String u = "max_tags_" + System.currentTimeMillis();
        String token = registerAndLogin(u, u + "@ex.com");
        String payload = "{" +
                "\"title\":\"Too many\"," +
                "\"body\":\"Body\"," +
                "\"tags\":[\"t1\",\"t2\",\"t3\",\"t4\",\"t5\",\"t6\"]" +
                "}";
        mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creating_question_with_invalid_tag_returns_400() throws Exception {
        String u = "bad_tag_" + System.currentTimeMillis();
        String token = registerAndLogin(u, u + "@ex.com");
        // invalid: spaces and uppercase and plus
        String payload = "{" +
                "\"title\":\"Bad tag\"," +
                "\"body\":\"Body\"," +
                "\"tags\":[\"C++\",\"Spring Boot\"]" +
                "}";
        mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}

