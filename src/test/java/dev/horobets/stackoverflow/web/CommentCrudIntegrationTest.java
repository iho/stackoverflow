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
class CommentCrudIntegrationTest {

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
    void comment_crud_on_question_and_answer_with_rbac() throws Exception {
        String user = "comm_owner_" + System.currentTimeMillis();
        String token = registerAndLogin(user, user + "@ex.com");

        // Create question
        String qResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Q with comments?\"," +
                                "\"body\":\"Body\"" +
                                "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long qId = objectMapper.readTree(qResp).get("id").asLong();

        // Create comment on question
        String cqResp = mockMvc.perform(post("/api/questions/" + qId + "/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"CQ1\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long cqId = objectMapper.readTree(cqResp).get("id").asLong();

        // Public list and get
        mockMvc.perform(get("/api/questions/" + qId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        mockMvc.perform(get("/api/comments/" + cqId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("CQ1"));

        // Create answer
        String aResp = mockMvc.perform(post("/api/questions/" + qId + "/answers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Ans\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long aId = objectMapper.readTree(aResp).get("id").asLong();

        // Create comment on answer
        String caResp = mockMvc.perform(post("/api/answers/" + aId + "/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"CA1\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long caId = objectMapper.readTree(caResp).get("id").asLong();

        // Public list and get
        mockMvc.perform(get("/api/answers/" + aId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        mockMvc.perform(get("/api/comments/" + caId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("CA1"));

        // Update by owner
        mockMvc.perform(put("/api/comments/" + caId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"CA1-upd\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("CA1-upd"));

        // Non-owner cannot update
        String other = "comm_other_" + System.currentTimeMillis();
        String otherToken = registerAndLogin(other, other + "@ex.com");
        mockMvc.perform(put("/api/comments/" + cqId)
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"nope\"}"))
                .andExpect(status().isForbidden());

        // Delete by owner
        mockMvc.perform(delete("/api/comments/" + cqId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/comments/" + cqId)).andExpect(status().isNotFound());
    }
}

