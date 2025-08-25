package dev.horobets.stackoverflow.model.user;

import dev.horobets.stackoverflow.model.BaseEntity;
import dev.horobets.stackoverflow.model.badge.UserBadge;
import dev.horobets.stackoverflow.model.bookmark.Bookmark;
import dev.horobets.stackoverflow.model.post.Answer;
import dev.horobets.stackoverflow.model.post.Question;
import dev.horobets.stackoverflow.model.vote.Vote;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "ix_users_username", columnList = "username", unique = true),
        @Index(name = "ix_users_email", columnList = "email", unique = true)
})
public class User extends BaseEntity {

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @NotBlank
    @Size(min = 6, max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private int reputation = 1;

    @Lob
    @Column(name = "about")
    private String about;

    @Column(name = "last_login")
    private Instant lastLogin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Answer> answers = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bookmark> bookmarks = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserBadge> badges = new HashSet<>();

    public User() {}

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public Instant getLastLogin() { return lastLogin; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public Set<Question> getQuestions() { return questions; }
    public void setQuestions(Set<Question> questions) { this.questions = questions; }
    public Set<Answer> getAnswers() { return answers; }
    public void setAnswers(Set<Answer> answers) { this.answers = answers; }
    public Set<Vote> getVotes() { return votes; }
    public void setVotes(Set<Vote> votes) { this.votes = votes; }
    public Set<Bookmark> getBookmarks() { return bookmarks; }
    public void setBookmarks(Set<Bookmark> bookmarks) { this.bookmarks = bookmarks; }
    public Set<UserBadge> getBadges() { return badges; }
    public void setBadges(Set<UserBadge> badges) { this.badges = badges; }
}
