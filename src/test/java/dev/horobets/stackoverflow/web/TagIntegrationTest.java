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
class TagIntegrationTest {

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
    void create_update_question_with_tags_and_verify_counts_and_listing() throws Exception {
        String user = "tagger-" + System.currentTimeMillis();
        String token = registerAndLogin(user, user + "@ex.com");
        String tagA = ("ta-" + System.currentTimeMillis()).toLowerCase();
        String tagB = ("tb-" + System.currentTimeMillis()).toLowerCase();
        String tagC = ("tc-" + System.currentTimeMillis()).toLowerCase();

        // Create question with tagA, tagB
        String createPayload = "{" +
                "\"title\":\"Tagged question\"," +
                "\"body\":\"Body\"," +
                "\"tags\":[\"" + tagA + "\",\"" + tagB + "\"]" +
                "}";
        String qResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags", containsInAnyOrder(tagA, tagB)))
                .andReturn().getResponse().getContentAsString();
        long qId = objectMapper.readTree(qResp).get("id").asLong();

        // Tag detail counts should be 1
        mockMvc.perform(get("/api/tags/" + tagA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionCount").value(1));
        mockMvc.perform(get("/api/tags/" + tagB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionCount").value(1));

        // Questions by tagA should include our question
        mockMvc.perform(get("/api/tags/" + tagA + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(qId));

        // Update question tags to tagA + tagC (remove tagB)
        String updatePayload = "{" +
                "\"title\":\"Tagged question\"," +
                "\"body\":\"Body\"," +
                "\"tags\":[\"" + tagA + "\",\"" + tagC + "\"]" +
                "}";
        mockMvc.perform(put("/api/questions/" + qId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags", containsInAnyOrder(tagA, tagC)));

        // Counts reflect changes: tagA still 1, tagB now 0, tagC 1
        mockMvc.perform(get("/api/tags/" + tagA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionCount").value(1));
        mockMvc.perform(get("/api/tags/" + tagB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionCount").value(0));
        mockMvc.perform(get("/api/tags/" + tagC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionCount").value(1));

        // Questions by tagB should be empty now
        mockMvc.perform(get("/api/tags/" + tagB + "/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
