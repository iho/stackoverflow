package dev.horobets.stackoverflow.web;

import dev.horobets.stackoverflow.model.bookmark.Bookmark;
import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.*;
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
class UserProfileIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Test
    void public_profile_aggregates_user_stats() throws Exception {
        // Register user via API with unique username/email to avoid collisions across tests
        String username = "bob_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String registerPayload = "{" +
                "\"username\":\"" + username + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"secret123\"" +
                "}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isOk());

        User bob = userRepository.findByUsername(username).orElseThrow();

        // Seed questions
        Question q1 = new Question();
        q1.setTitle("How to test endpoint?");
        q1.setBody("Body1");
        q1.setUser(bob);
        q1.setVoteCount(5);
        q1.setViews(100);
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setTitle("Another question");
        q2.setBody("Body2");
        q2.setUser(bob);
        q2.setVoteCount(2);
        q2.setViews(40);
        questionRepository.save(q2);

        // Seed answers
        Answer a1 = new Answer();
        a1.setBody("Answer 1");
        a1.setUser(bob);
        a1.setQuestion(q1);
        a1.setAccepted(true);
        a1.setVoteCount(3);
        answerRepository.save(a1);

        Answer a2 = new Answer();
        a2.setBody("Answer 2");
        a2.setUser(bob);
        a2.setQuestion(q1);
        a2.setAccepted(true);
        a2.setVoteCount(1);
        answerRepository.save(a2);

        Answer a3 = new Answer();
        a3.setBody("Answer 3");
        a3.setUser(bob);
        a3.setQuestion(q2);
        a3.setAccepted(false);
        a3.setVoteCount(0);
        answerRepository.save(a3);

        // Seed a bookmark
        Bookmark bm = new Bookmark();
        bm.setUser(bob);
        bm.setQuestion(q1);
        bookmarkRepository.save(bm);

        // Call public profile
        mockMvc.perform(get("/api/users/" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.reputation", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.roles", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.lastLogin", anyOf(nullValue(), notNullValue())))
                .andExpect(jsonPath("$.questionCount").value(2))
                .andExpect(jsonPath("$.answerCount").value(3))
                .andExpect(jsonPath("$.acceptedAnswerCount").value(2))
                .andExpect(jsonPath("$.votesReceived").value(5 + 2 + 3 + 1 + 0))
                .andExpect(jsonPath("$.questionViews").value(100 + 40))
                .andExpect(jsonPath("$.bookmarkCount").value(1))
                .andExpect(jsonPath("$.badgeCount", greaterThanOrEqualTo(0)));
    }

    @Test
    void profile_not_found_returns_404() throws Exception {
        mockMvc.perform(get("/api/users/unknown-user"))
                .andExpect(status().isNotFound());
    }
}
