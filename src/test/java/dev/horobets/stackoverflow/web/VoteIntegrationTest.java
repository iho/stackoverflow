package dev.horobets.stackoverflow.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.AnswerRepository;
import dev.horobets.stackoverflow.repository.QuestionRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
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
class VoteIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired QuestionRepository questionRepository;
    @Autowired AnswerRepository answerRepository;
    @Autowired UserRepository userRepository;

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
    void vote_crud_on_question_and_answer() throws Exception {
        // register and login
        String username = "voter_" + System.currentTimeMillis();
        String email = username + "@ex.com";
        String token = registerAndLogin(username, email);

        // create question
        String qResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Vote me?\"," +
                                "\"body\":\"Body\"" +
                                "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long qId = objectMapper.readTree(qResp).get("id").asLong();

        // GET vote status public
        mockMvc.perform(get("/api/votes/question/" + qId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.myVote").doesNotExist());

        // upvote
        mockMvc.perform(post("/api/votes/question/" + qId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"voteValue\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.myVote").value(1));

        // flip to downvote
        mockMvc.perform(post("/api/votes/question/" + qId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"voteValue\":-1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(-1))
                .andExpect(jsonPath("$.myVote").value(-1));

        // delete vote
        mockMvc.perform(delete("/api/votes/question/" + qId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.myVote").doesNotExist());

        // create answer via API
        String aResp = mockMvc.perform(post("/api/questions/" + qId + "/answers")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"Ans body\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long aId = objectMapper.readTree(aResp).get("id").asLong();

        // upvote answer
        mockMvc.perform(post("/api/votes/answer/" + aId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"voteValue\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.myVote").value(1));

        // GET without auth shows total, myVote missing
        mockMvc.perform(get("/api/votes/answer/" + aId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.myVote").doesNotExist());
    }
}

