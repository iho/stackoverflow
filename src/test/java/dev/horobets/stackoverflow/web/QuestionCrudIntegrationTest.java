package dev.horobets.stackoverflow.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.horobets.stackoverflow.model.user.Role;
import dev.horobets.stackoverflow.model.user.RoleName;
import dev.horobets.stackoverflow.model.user.User;
import dev.horobets.stackoverflow.repository.RoleRepository;
import dev.horobets.stackoverflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuestionCrudIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    String ownerUsername;
    String ownerToken;

    @BeforeEach
    void setupOwner() throws Exception {
        ownerUsername = "owner_" + System.currentTimeMillis();
        String email = ownerUsername + "@ex.com";
        // register
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + ownerUsername + "\"," +
                                "\"email\":\"" + email + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk());
        // login
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + ownerUsername + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResp);
        ownerToken = loginJson.get("tokenType").asText() + " " + loginJson.get("token").asText();
        assertThat(ownerToken).isNotBlank();
    }

    @Test
    void create_then_get_public_and_list() throws Exception {
        // create
        String payload = "{" +
                "\"title\":\"First question?\"," +
                "\"body\":\"Body text\"" +
                "}";
        String createResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/questions/")))
                .andReturn().getResponse().getContentAsString();
        JsonNode created = objectMapper.readTree(createResp);
        long id = created.get("id").asLong();

        // get without auth
        mockMvc.perform(get("/api/questions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("First question?"))
                .andExpect(jsonPath("$.authorUsername").value(ownerUsername));

        // list public
        mockMvc.perform(get("/api/questions")).andExpect(status().isOk());
    }

    @Test
    void update_forbidden_for_non_owner() throws Exception {
        // create as owner
        String payload = "{" +
                "\"title\":\"Owner question\"," +
                "\"body\":\"Body\"" +
                "}";
        String createResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createResp).get("id").asLong();

        // register other user and login
        String other = "other_" + System.currentTimeMillis();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + other + "\"," +
                                "\"email\":\"" + other + "@ex.com\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk());
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + other + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResp);
        String otherToken = loginJson.get("tokenType").asText() + " " + loginJson.get("token").asText();

        // attempt update should be 403
        mockMvc.perform(put("/api/questions/" + id)
                        .header("Authorization", otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Hacked\"," +
                                "\"body\":\"No\"" +
                                "}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void owner_can_update_and_delete_and_soft_delete_hides_question() throws Exception {
        // create
        String payload = "{" +
                "\"title\":\"Owner question\"," +
                "\"body\":\"Body\"" +
                "}";
        String createResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createResp).get("id").asLong();

        // update ok
        mockMvc.perform(put("/api/questions/" + id)
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Updated\"," +
                                "\"body\":\"New body\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));

        // delete ok
        mockMvc.perform(delete("/api/questions/" + id)
                        .header("Authorization", ownerToken))
                .andExpect(status().isNoContent());

        // get should be 404 (soft deleted and filtered)
        mockMvc.perform(get("/api/questions/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void moderator_can_update_and_delete() throws Exception {
        // create as owner
        String payload = "{" +
                "\"title\":\"Moderate me\"," +
                "\"body\":\"Body\"" +
                "}";
        String createResp = mockMvc.perform(post("/api/questions")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(createResp).get("id").asLong();

        // create moderator user and grant role
        String modUser = "mod_" + System.currentTimeMillis();
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + modUser + "\"," +
                                "\"email\":\"" + modUser + "@ex.com\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk());
        User u = userRepository.findWithRolesByUsername(modUser).orElseThrow();
        Role modRole = roleRepository.findByName(RoleName.ROLE_MODERATOR).orElseThrow();
        u.getRoles().add(modRole);
        userRepository.save(u);
        // login mod
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"username\":\"" + modUser + "\"," +
                                "\"password\":\"secret123\"" +
                                "}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResp);
        String modToken = loginJson.get("tokenType").asText() + " " + loginJson.get("token").asText();

        // mod can update
        mockMvc.perform(put("/api/questions/" + id)
                        .header("Authorization", modToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Moderated\"," +
                                "\"body\":\"OK\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Moderated"));

        // mod can delete
        mockMvc.perform(delete("/api/questions/" + id)
                        .header("Authorization", modToken))
                .andExpect(status().isNoContent());
    }
}
