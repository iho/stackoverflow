package dev.horobets.stackoverflow.web.dto;

import java.time.Instant;
import java.util.Set;

public class MeResponse {
    private String username;
    private String email;
    private int reputation;
    private Set<String> roles;
    private Instant createdAt;

    public MeResponse() {}

    public MeResponse(String username, String email, int reputation, Set<String> roles, Instant createdAt) {
        this.username = username;
        this.email = email;
        this.reputation = reputation;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

