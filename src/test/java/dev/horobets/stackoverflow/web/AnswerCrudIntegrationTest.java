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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnswerCrudIntegrationTest {

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
    void create_list_get_update_delete_answer_and_answerCount() throws Exception {
        String username = "ans_owner_" + System.currentTimeMillis();
        String email = username + "@ex.com";
        String token = registerAndLogin(username, email);

        // Create question
        String qResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Q with answers?\"," +
                                "\"body\":\"Body\"" +
                                "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long qId = objectMapper.readTree(qResp).get("id").asLong();

        // Create two answers
        String a1Resp = mockMvc.perform(post("/api/questions/" + qId + "/answers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"A1\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long a1 = objectMapper.readTree(a1Resp).get("id").asLong();

        String a2Resp = mockMvc.perform(post("/api/questions/" + qId + "/answers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"A2\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long a2 = objectMapper.readTree(a2Resp).get("id").asLong();

        // List answers (public)
        mockMvc.perform(get("/api/questions/" + qId + "/answers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))));

        // Get single answer (public)
        mockMvc.perform(get("/api/answers/" + a1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("A1"));

        // Update by owner
        mockMvc.perform(put("/api/answers/" + a1)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"A1-upd\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("A1-upd"));

        // Non-owner cannot update
        String other = "ans_other_" + System.currentTimeMillis();
        String otherToken = registerAndLogin(other, other + "@ex.com");
        mockMvc.perform(put("/api/answers/" + a2)
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"nope\"}"))
                .andExpect(status().isForbidden());

        // Delete by owner
        mockMvc.perform(delete("/api/answers/" + a2)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        // Question answerCount decreased, and answer gone
        mockMvc.perform(get("/api/answers/" + a2)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/questions/" + qId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerCount", greaterThanOrEqualTo(1)));
    }
}

